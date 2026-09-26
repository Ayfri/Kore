plugins {
	kotlin("multiplatform")
	kotlin("plugin.serialization")
	id("com.google.devtools.ksp")
	alias(libs.plugins.kotest)
	id("kotest-conventions")
	id("kotlin-conventions")
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
	jvm()
	js {
		browser()
		nodejs()
	}
	jvmToolchain(25)

	compilerOptions {
		freeCompilerArgs.addAll(listOf("-Xrender-internal-diagnostic-names"))
	}

	sourceSets {
		commonMain.dependencies {
			api(project(":kore"))
			implementation(libs.kotlinx.io)
			implementation(libs.kotlinx.serialization)
		}

		jvmMain.dependencies {
			implementation(libs.kotlinpoet)
		}

		commonTest.dependencies {
			implementation(project(":common-tests"))
		}

		jvmTest.dependencies {
			implementation(libs.kotlin.dotenv)
		}
	}
}
