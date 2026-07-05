plugins {
	kotlin("multiplatform")
	kotlin("plugin.serialization")
	alias(libs.plugins.ksp)
	id("kotest-conventions")
	id("publish-conventions")
}

metadata {
	name = "Kore"
	description = "A Kotlin DSL to create Minecraft datapacks."
}

repositories {
	mavenCentral()
}

kotlin {
	jvm()
	js {
		browser()
		nodejs()
	}
	jvmToolchain(25)

	compilerOptions {
		freeCompilerArgs.addAll(listOf("-Xrender-internal-diagnostic-names"))
	}

	sourceSets {
		commonMain {
			dependencies {
				implementation(libs.kotlinx.io)
				implementation(libs.kotlinx.serialization)
				api(libs.knbt)
			}
			kotlin.srcDir(layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))
		}

		jvmMain.dependencies {
			implementation(libs.ktoml)
		}

		jvmTest.dependencies {
			implementation(libs.kotlin.dotenv)
		}
	}
}

dependencies {
	add("kspCommonMainMetadata", project(":kore-ksp"))
	add("kspJvmTest", project(":kore-ksp"))
}

tasks.named("compileKotlinJvm") {
	val generatedFolder = file("src/commonMain/kotlin/io/github/ayfri/kore/generated")
	if (!generatedFolder.exists()) {
		dependsOn(":generation:run")
	}
}

// The commonMain source set manually adds the KSP metadata output folder as an extra source directory
// (see `kotlin.srcDir(...)` above), which Gradle doesn't automatically recognize as a task output/input
// link. Every task that compiles or packages commonMain sources must therefore be told explicitly to wait
// for `kspCommonMainKotlinMetadata`, or builds fail (or silently race) with "implicit dependency" errors -
// this bit everyone publishing the KMP `kore` module, not just `compileKotlinJvm`/`compileKotlinJs`.
tasks.matching {
	it.name != "kspCommonMainKotlinMetadata" &&
		(it.name.startsWith("compileKotlin") || it.name.contains("SourcesJar", ignoreCase = true))
}.configureEach {
	dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
}
