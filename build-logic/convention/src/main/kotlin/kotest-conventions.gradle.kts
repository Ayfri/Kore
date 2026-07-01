import gradle.kotlin.dsl.accessors._511028b7c35ba999a76ba725df8506cc.testImplementation
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
	testImplementation(libs.findLibrary("kotest-assertions-core").get())
	testImplementation(libs.findLibrary("kotest-runner-junit5").get())
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