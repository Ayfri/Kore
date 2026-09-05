import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
	extensions.configure<KotlinMultiplatformExtension> {
		sourceSets.matching { it.name == "commonTest" }.configureEach {
			dependencies {
				implementation(libs.findLibrary("kotest-framework-engine").get())
				implementation(libs.findLibrary("kotest-assertions-core").get())
			}
		}
		sourceSets.matching { it.name == "jvmTest" }.configureEach {
			dependencies {
				implementation(libs.findLibrary("kotest-runner-junit5").get())
			}
		}
	}
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
	dependencies {
		add("testImplementation", libs.findLibrary("kotest-assertions-core").get())
		add("testImplementation", libs.findLibrary("kotest-runner-junit5").get())
	}
}

// Browser and Node run the same JS IR, so the Karma round-trip is opt-in via `-Pkore.jsBrowserTests=true`.
// It has to be `onlyIf` rather than `enabled`: a disabled task stops contributing its npm dependencies, which would
// make `kotlin-js-store/package-lock.json` differ between the two modes.
val jsBrowserTests = providers.gradleProperty("kore.jsBrowserTests").map(String::toBoolean).getOrElse(false)

tasks.matching { it.name == "jsBrowserTest" }.configureEach {
	val runBrowserTests = jsBrowserTests // Copied into a task-local so the spec below captures a value, not the script.
	onlyIf { runBrowserTests }
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()

	testLogging {
		events("failed", "standardError")
		exceptionFormat = TestExceptionFormat.FULL
		showExceptions = true
		showCauses = true
		showStackTraces = true
	}
}
