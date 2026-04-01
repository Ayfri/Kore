plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("kotest-conventions")
	id("publish-conventions")
}

metadata {
	name = "Kore Bindings"
	description = "Imports existing Minecraft datapacks and generates type-safe Kotlin bindings."
}

repositories {
	mavenCentral()
}

kotlin {
	jvmToolchain(21)

	compilerOptions {
		freeCompilerArgs =
			listOf("-Xcontext-parameters", "-Xrender-internal-diagnostic-names")
	}
}

dependencies {
	implementation(libs.kotlinpoet)
	implementation(libs.kotlinx.serialization)
	implementation(project(":kore"))

	testImplementation(libs.kotlin.dotenv)
	testImplementation(libs.kotlinx.io)
}
