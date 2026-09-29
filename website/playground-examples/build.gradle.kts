plugins {
	kotlin("jvm")
	id("kotlin-conventions")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":kore"))
}

kotlin {
	jvmToolchain(25)
}

val exampleSources = layout.projectDirectory.dir("src/main/kotlin/io/github/ayfri/kore/website/playground/snippets")

/** Read by `website/build.gradle.kts`: `kotlin/` holds the example list, `resources/public/` one generated pack per example. */
val generatedDir = layout.buildDirectory.dir("generated/playground")

// Runs each example on the JVM, so the playground shows an untouched example's pack without compiling anything.
tasks.register<JavaExec>("generatePlaygroundExamples") {
	group = "kore"
	description = "Runs every playground example and writes its source and generated files for the website."
	mainClass = "io.github.ayfri.kore.website.playground.MainKt"
	classpath = sourceSets.main.get().runtimeClasspath

	inputs.dir(exampleSources).withPropertyName("exampleSources").withPathSensitivity(PathSensitivity.RELATIVE)
	outputs.dir(generatedDir).withPropertyName("generatedDir")
	outputs.cacheIf { true }

	val sourcesPath = exampleSources.asFile.absolutePath
	val outputPath = generatedDir.map { it.asFile.absolutePath }
	argumentProviders += CommandLineArgumentProvider { listOf(sourcesPath, outputPath.get()) }
}
