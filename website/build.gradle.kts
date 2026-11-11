import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.tasks.ProcessMarkdownTask
import kotlinx.html.LinkAs
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.unsafe
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import website.DocSiteGenerator
import website.FetchGitHubReleases
import website.FetchGitHubStars
import website.FetchMinecraftVersions
import website.useSiteComponents
import kotlin.time.Duration.Companion.seconds

plugins {
	kotlin("multiplatform")
	kotlin("plugin.compose")
	id("kotlin-conventions")
	alias(libs.plugins.kobweb.application)
	alias(libs.plugins.kobwebx.markdown)
}

group = "io.github.ayfri.kore.website"
version = "1.0-SNAPSHOT"

val docGroupOrder =
	listOf("guides", "commands", "data-driven", "concepts", "helpers", "oop", "advanced", "contributing")

val minecraftVersion = providers.gradleProperty("minecraft.version").orElse("").get()

/** GitHub data written by the fetch tasks (stars, releases, Minecraft versions data), read by the Open Graph cards, `llms.txt` and the doc placeholders. */
val gitHubDataDir = layout.buildDirectory.dir("generated/github")

val generatedSources = layout.buildDirectory.dir("generated/kore/src/jsMain/kotlin")

/** The playground example list and each example's pack, written by `:playground-examples:generatePlaygroundExamples`. */
val playgroundExamplesDir = layout.projectDirectory.dir("playground-examples/build/generated/playground")

/** `public/playground-api.json`, the Kore API the playground editor completes, written by `:playground-examples:generatePlaygroundApiIndex`. */
val playgroundApiDir = layout.projectDirectory.dir("playground-examples/build/generated/playground-api")

/** The home page widget source the `FeatureGrid` placeholder of the docs is built from. */
val featureCards = layout.projectDirectory.file("src/jsMain/kotlin/io/github/ayfri/kore/website/components/doc/FeatureCards.kt")

val logo = layout.projectDirectory.file("src/jsMain/resources/public/logo.png")

/** Open Graph cards only ship in the exported site, rendering them on every Markdown edit of the dev server costs seconds. */
val renderOgImages = gradle.startParameter.taskNames.any { it.substringAfterLast(':') == "kobwebExport" }

/** Kobweb serves `public/` of the folder where `kobwebxMarkdownProcess` writes its public resources, the cards go straight into it. */
val ogDir = tasks.named<ProcessMarkdownTask>("kobwebxMarkdownProcess")
	.flatMap { it.getGenResPublicDir() }
	.map { it.dir("public/og").asFile }

kobweb {
	app {
		globals.set(
			mapOf(
				"docGroupOrder" to docGroupOrder.joinToString(","),
				"minecraftVersion" to minecraftVersion,
				// Compile backend for /playground. Empty here: the page stays usable and says compiling is off.
				"playgroundApiUrl" to (findProperty("kore.playgroundApiUrl") as String? ?: ""),
				"projectVersion" to Project.VERSION,
				"websiteUrl" to Project.WEBSITE_URL,
			)
		)

		index {
			// Deferred scripts run in document order, so the deferred Prism of the head is ready before the app starts.
			scriptAttributes.put("defer", "")

			head.add {
				// The exported pages paint without the app, but its bundle sits at the end of a body that reaches 500 KB on /updates.
				link("/website.js", "preload", htmlAs = LinkAs.script)
				// The text and heading fonts show above the fold on every page, the code font only loads once a code block renders.
				for (file in listOf("ibm-plex-sans", "sora")) {
					link("/fonts/$file.woff2", "preload", "font/woff2", LinkAs.font) {
						attributes["crossorigin"] = "anonymous"
					}
				}
				// Latin subsets of Google Fonts' variable files, other glyphs fall back to the next font of the stack.
				style {
					unsafe {
						+"""
							@font-face{font-display:swap;font-family:"IBM Plex Sans";font-weight:400 700;src:url(/fonts/ibm-plex-sans.woff2) format("woff2")}
							@font-face{font-display:swap;font-family:"JetBrains Mono";font-style:italic;font-weight:400;src:url(/fonts/jetbrains-mono-italic.woff2) format("woff2")}
							@font-face{font-display:swap;font-family:"JetBrains Mono";font-weight:400 700;src:url(/fonts/jetbrains-mono.woff2) format("woff2")}
							@font-face{font-display:swap;font-family:"Sora";font-weight:600 700;src:url(/fonts/sora.woff2) format("woff2")}
						""".trimIndent().replace("\n", "")
					}
				}
				link("/view-transitions.css", "stylesheet")
				link("/llms.txt", "describedby")

				script("text/javascript", "/prism.min.js") {
					attributes["data-manual"] = ""
					defer = true
				}

				// `pagereveal` fires before website.js runs: turns the doc direction `PageTransitions` stores on `pageswap` into a transition type.
				script("text/javascript") {
					unsafe {
						+"""addEventListener("pagereveal",e=>{const h=sessionStorage.getItem("kore-view-transition");if(h===null)return;sessionStorage.removeItem("kore-view-transition");const[t,p]=h.split(" ");if(p===location.pathname)e.viewTransition?.types?.add(t)})"""
					}
				}

				script("text/javascript", "https://www.googletagmanager.com/gtag/js?id=G-3ZXF56FSLH") {
					async = true
				}
				script("text/javascript") {
					unsafe {
						+"""function gtag(){dataLayer.push(arguments)}window.dataLayer=window.dataLayer||[],gtag("js",new Date),gtag("config","G-3ZXF56FSLH")"""
					}
				}
			}
		}

		export {
			includeSourceMap = false
			// Playwright's 30s default leaves no headroom once several pages are snapshotted at once.
			timeout = 90.seconds
		}
	}

	markdown {
		imports.add("com.varabyte.kobweb.compose.ui.modifiers.*")

		handlers { useSiteComponents() }

		/** Built outside the callback, which the configuration cache serializes with what it captures. */
		val generator = DocSiteGenerator(
			docGroupOrder = docGroupOrder,
			featureCards = featureCards.asFile,
			gitHubDataDir = gitHubDataDir.get().asFile,
			logger = logger,
			logo = logo.asFile,
			markdownDir = projectDir.resolve("src/jsMain/resources/markdown"),
			minecraftVersion = minecraftVersion,
			ogDir = ogDir.get(),
			ogFontsDir = layout.buildDirectory.dir("og-fonts").get().asFile,
			renderOgImages = renderOgImages,
		)
		process.set { pages -> generator.generate(this, pages) }
	}
}

// Monaco's ESM build is bundled ahead of time by esbuild into plain static assets, never with the site
// script: `kobwebExport` copies exactly one script file, so any code-split chunk would 404 in
// production, and a static import would instead drag all ~6 MB of Monaco into the main bundle on every page.
// The scripts are staged next to the Kotlin/JS `node_modules` so bare imports like `monaco-editor/...`
// resolve, for both esbuild and Node itself.
val stageMonacoBuild = tasks.register<Copy>("stageMonacoBuild") {
	group = "kore"
	description = "Stages the Monaco esbuild scripts next to the Kotlin/JS node_modules."

	dependsOn(rootProject.tasks.named("kotlinNpmInstall"))

	from(projectDir.resolve("monaco"))
	into(rootProject.layout.buildDirectory.dir("js/monaco-build"))
}

val bundleMonaco = tasks.register<Exec>("bundleMonaco") {
	group = "kore"
	description = "Bundles Monaco's ESM distribution into the public resources, served at /monaco."

	dependsOn(stageMonacoBuild)

	val buildDir = rootProject.layout.buildDirectory.dir("js/monaco-build")
	val outDir = projectDir.resolve("src/jsMain/resources/public/monaco")
	val nodeExecutable = rootProject.extensions.getByType<NodeJsEnvSpec>().executable

	inputs.dir(projectDir.resolve("monaco"))
	inputs.property("monacoVersion", libs.versions.monaco.editor.get())
	outputs.dir(outDir)

	workingDir(buildDir)
	commandLine(nodeExecutable.get(), "build-monaco.mjs", outDir.absolutePath)
}

val fetchGitHubReleases = tasks.register<FetchGitHubReleases>("fetchGitHubReleases") {
	latestTagFile = gitHubDataDir.map { it.file("latest-release-tag.txt") }
	releasesFile = gitHubDataDir.map { it.file("releases.tsv") }
	sourceFile = generatedSources.map { it.file("io/github/ayfri/kore/website/gitHubReleases.kt") }
}

val fetchGitHubStars = tasks.register<FetchGitHubStars>("fetchGitHubStars") {
	sourceFile = generatedSources.map { it.file("io/github/ayfri/kore/website/gitHubStars.kt") }
	starsFile = gitHubDataDir.map { it.file("stars.txt") }
}

val fetchMinecraftVersions = tasks.register<FetchMinecraftVersions>("fetchMinecraftVersions") {
	versionsFile = gitHubDataDir.map { it.file("versions_data.json") }
}

// Kobweb's processor reads annotated functions and `CssStyle` or `Keyframes` properties, never a body. The declaration scan
// doesn't see such a property, the site styles everything with Compose `StyleSheet`s.
skipKspUntilDeclarationsChange("kspKotlinJs")

/** The generated release and star sources, and the playground examples, exist before KSP and the compiler read the source set. */
tasks.matching { it.name in setOf("compileKotlinJs", "kspKotlinJs") }.configureEach {
	dependsOn(fetchGitHubReleases, fetchGitHubStars, ":playground-examples:generatePlaygroundExamples")
}

tasks.matching { it.name == "jsProcessResources" }.configureEach {
	dependsOn(bundleMonaco, ":playground-examples:generatePlaygroundApiIndex", ":playground-examples:generatePlaygroundExamples")
}

/**
 * What this script hands to the generator, which Gradle can't read from the `process` lambda. The doc placeholders read the releases
 * and the pack formats, the Open Graph cards show the logo, the star count and the latest release.
 */
tasks.named("kobwebxMarkdownProcess") {
	inputs.files(featureCards, logo, fetchGitHubReleases, fetchGitHubStars, fetchMinecraftVersions).withPropertyName("generatorFiles")
	inputs.property("docGroupOrder", docGroupOrder)
	inputs.property("minecraftVersion", minecraftVersion)
	inputs.property("renderOgImages", renderOgImages)
}

kotlin {
	configAsKobwebApplication("website")

	js {
		compilerOptions {
			target = "es2015"
			useEsClasses = true
		}
		useEsModules()
	}

	sourceSets {
		jsMain {
			kotlin.srcDir(generatedSources)
			kotlin.srcDir(playgroundExamplesDir.dir("kotlin"))
			resources.srcDir(playgroundApiDir.dir("resources"))
			resources.srcDir(playgroundExamplesDir.dir("resources"))

			dependencies {
				// esbuild bundles the site script and Monaco (see bundleMonaco), which is never imported from Kotlin:
				// both packages are build-time only.
				implementation(devNpm("esbuild", libs.versions.esbuild.get()))
				implementation(devNpm("monaco-editor", libs.versions.monaco.editor.get()))
			}
		}
		commonMain {
			dependencies {
				implementation(libs.compose.html.core)
				implementation(libs.compose.runtime)
				implementation(libs.kobweb.core)
				implementation(libs.kobwebx.markdown)
				implementation(libs.kobwebx.silk.icons.lucide)
				implementation(npm("marked", libs.versions.marked.get()))
			}
		}
	}
}

/**
 * esbuild bundles the modules the Kotlin compiler links, in place of webpack: 1.7 s where webpack takes 10 s for the dev script and
 * 1 s where it takes 15 s for the minified one, at the same brotli size. Its source maps point at the Kotlin files.
 */
mapOf(
	"Development" to listOf("--sourcemap=linked"),
	"Production" to listOf("--legal-comments=none", "--minify", "--target=es2020"),
).forEach { (mode, options) ->
	val bundle = tasks.register<Exec>("bundle${mode}Site") {
		group = "kore"
		description = "Bundles the ${mode.lowercase()} site script with esbuild, where `.kobweb/conf.yaml` reads it."

		val linked = tasks.named<KotlinJsIrLink>("compile${mode}ExecutableKotlinJs").flatMap { it.destinationDirectory }
		val nodeModules = rootProject.layout.buildDirectory.dir("js/node_modules").get()
		val script = layout.buildDirectory.file("site-bundle/${mode.lowercase()}/website.js")

		dependsOn(rootProject.tasks.named("kotlinNpmInstall"))
		inputs.dir(linked).withPropertyName("linked")
		inputs.property("esbuildVersion", libs.versions.esbuild.get())
		outputs.dir(script.map { it.asFile.parentFile }).withPropertyName("bundle")

		// The linked modules sit outside the npm workspace, `marked` resolves through NODE_PATH.
		environment("NODE_PATH", nodeModules.asFile.path)
		executable(rootProject.extensions.getByType<NodeJsEnvSpec>().executable.get())
		args(nodeModules.file("esbuild/bin/esbuild").asFile.path, "--bundle", "--format=iife", "--log-level=warning")
		args(options)
		argumentProviders += CommandLineArgumentProvider {
			listOf(linked.get().file("website.mjs").asFile.path, "--outfile=${script.get().asFile.path}")
		}
	}

	// Kobweb's start and export tasks depend on the webpack task by name: it stays as an empty shell pulling the bundle in, and the
	// copy of the linked modules into the npm workspace, made for webpack alone, is skipped with it.
	tasks.named("js${mode}ExecutableCompileSync") {
		enabled = false
	}
	tasks.named("jsBrowser${mode}Webpack") {
		enabled = false
		dependsOn(bundle)
	}
}
