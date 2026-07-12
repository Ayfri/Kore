plugins {
	kotlin("multiplatform")
	kotlin("plugin.serialization")
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
			api(libs.kotest.assertions.core)
			implementation(libs.kotlinx.io)
			implementation(libs.kotlinx.serialization)
		}

		jvmMain.dependencies {
			implementation(libs.kotlin.dotenv)
		}
	}
}
