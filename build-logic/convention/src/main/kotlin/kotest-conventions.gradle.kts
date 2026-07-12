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

// Aggregates every module's `allTests` (itself covering every KMP target: jvmTest, jsNodeTest, jsBrowserTest)
// into a single root task, so CI doesn't need to enumerate modules or platforms.
val testAll = rootProject.tasks.findByName("testAll")
	?: rootProject.tasks.create("testAll") {
		group = "verification"
		description = "Runs every module's tests across all Kotlin Multiplatform targets (JVM, JS Node, JS Browser)."
	}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
	testAll.dependsOn("${project.path}:allTests")
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
