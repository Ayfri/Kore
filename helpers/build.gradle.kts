plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("kotest-conventions")
	id("publish-conventions")
}

metadata {
	name = "Helpers"
	description = "Helper utilities built on top of Kore and its OOP abstractions."
}

repositories {
	mavenCentral()
}

dependencies {
	api(project(":oop"))
	implementation(libs.kotlinx.io)
	implementation(libs.kotlinx.serialization)
}

kotlin {
	jvmToolchain(21)

	compilerOptions {
		freeCompilerArgs = listOf("-Xcontext-parameters")
	}
}