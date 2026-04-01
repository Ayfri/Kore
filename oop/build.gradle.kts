plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("kotest-conventions")
	id("publish-conventions")
}

metadata {
	name = "OOP"
	description = "An OOP form of Kore, experimental and not complete."
}

repositories {
	mavenCentral()
}

dependencies {
	api(project(":kore"))
	implementation(libs.kotlinx.io)
	implementation(libs.kotlinx.serialization)
}

kotlin {
	jvmToolchain(21)

	compilerOptions {
		freeCompilerArgs = listOf("-Xcontext-parameters")
	}
}
