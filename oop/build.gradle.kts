plugins {
	kotlin("multiplatform")
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

kotlin {
	jvm()
	js {
		browser()
		nodejs()
	}
	jvmToolchain(25)

	sourceSets {
		commonMain.dependencies {
			api(project(":kore"))
			implementation(libs.kotlinx.io)
			implementation(libs.kotlinx.serialization)
		}
	}
}
