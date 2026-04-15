import org.gradle.api.tasks.testing.logging.TestExceptionFormat

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
	add("testImplementation", libs.findLibrary("kotest-assertions-core").get())
	add("testImplementation", libs.findLibrary("kotest-runner-junit5").get())
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