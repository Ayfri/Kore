plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	application
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.ktor.client.core)
	implementation(libs.ktor.client.cio)
	implementation(libs.ktor.client.content.negotiation)
	implementation(libs.ktor.serialization)
	implementation(libs.ktor.serialization.kotlinx.json)

	implementation(libs.kotlinpoet)
}

kotlin {
	jvmToolchain(25)
}

application {
	mainClass = "MainKt"
}

// Cacheable alternative to `run`: same JavaExec, but with declared inputs/outputs so Gradle can skip it (locally or
// via the remote build cache) when `minecraft.version` hasn't changed since the last successful run.
val generateSources by tasks.registering(JavaExec::class) {
	group = "build"
	description = "Regenerates kore's generated sources from Minecraft data."
	mainClass = application.mainClass
	classpath = sourceSets.main.get().runtimeClasspath

	inputs.property("minecraftVersion", providers.gradleProperty("minecraft.version"))
	outputs.dir(rootProject.file("kore/src/commonMain/kotlin/io/github/ayfri/kore/generated"))
	outputs.cacheIf { true }
}
