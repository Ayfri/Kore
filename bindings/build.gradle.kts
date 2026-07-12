plugins {
	kotlin("multiplatform")
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
			implementation(libs.kompress.core)
			implementation(libs.kompress.zip)
			implementation(libs.kotlinx.io)
			implementation(libs.kotlinx.serialization)
		}

		jvmMain.dependencies {
			implementation(libs.kotlinpoet)
		}

		jvmTest.dependencies {
			implementation(libs.kotlin.dotenv)
		}
	}
}

// commonTest is exercised on the js() target via compilation only (no io.kotest multiplatform
// test-discovery plugin wired up in this repo yet); don't fail :build over that missing runner.
tasks.matching { it.name == "jsNodeTest" || it.name == "jsBrowserTest" }.configureEach {
	(this as? org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest)?.failOnNoDiscoveredTests?.set(false)
}
