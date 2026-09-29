plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("kotlin-conventions")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":helpers"))
	implementation(project(":kore"))
	implementation(project(":oop"))
	implementation(libs.kotlin.compiler.embeddable)
	implementation(libs.kotlin.metadata.jvm)
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

/** Read by `website/build.gradle.kts`: `resources/public/playground-api.json`, the API the playground editor completes. */
val apiIndexDir = layout.buildDirectory.dir("generated/playground-api")

/** The modules the compile backend puts on the snippet's classpath, as their common sources: jvmMain-only API never reaches a browser. */
val apiSourceRoots = listOf("helpers", "kore", "oop").map { "$it/src/commonMain/kotlin" }

// Types come from the compiled metadata, which knows inferred return types; docs, defaults and lines from the sources.
tasks.register<JavaExec>("generatePlaygroundApiIndex") {
	group = "kore"
	description = "Indexes the public API of kore, oop and helpers for the playground's completion, hover docs and auto-import."
	mainClass = "io.github.ayfri.kore.website.playground.api.ApiIndexKt"
	classpath = sourceSets.main.get().runtimeClasspath

	val repository = rootProject.layout.projectDirectory
	val libraries = configurations.runtimeClasspath.get().incoming.artifactView {
		componentFilter { it is ProjectComponentIdentifier }
	}.files

	inputs.files(apiSourceRoots.map(repository::dir)).withPropertyName("sources").withPathSensitivity(PathSensitivity.RELATIVE)
	outputs.dir(apiIndexDir).withPropertyName("apiIndexDir")
	outputs.cacheIf { true }

	val repositoryPath = repository.asFile.absolutePath
	val roots = apiSourceRoots.joinToString(",")
	val outputPath = apiIndexDir.map { it.file("resources/public/playground-api.json").asFile.absolutePath }
	argumentProviders += CommandLineArgumentProvider {
		listOf(outputPath.get(), repositoryPath, roots, libraries.joinToString(File.pathSeparator))
	}
}
