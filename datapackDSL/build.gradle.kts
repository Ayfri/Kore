import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	application
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
	implementation(kotlin("reflect"))
	api("net.benwoodworth.knbt:knbt:0.11.3")
	api("org.joml:joml:1.10.5")

	testImplementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
}

kotlin {
	jvmToolchain(18)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xcontext-receivers")
	}
}

application {
	mainClass.set("MainKt")
}
