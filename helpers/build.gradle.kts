plugins {
	kotlin("multiplatform")
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

kotlin {
	jvm()
	js {
		browser()
		nodejs()
	}
	jvmToolchain(25)

	sourceSets {
		commonMain.dependencies {
			api(project(":oop"))
			implementation(libs.kotlinx.io)
			implementation(libs.kotlinx.serialization)
		}

		jvmTest.dependencies {
			implementation(libs.joml)
		}
	}
}
