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
		commonMain {
			dependencies {
				api(project(":oop"))
				implementation(libs.kotlinx.io)
				implementation(libs.kotlinx.serialization)
			}
			// KSP doesn't expose common-metadata output as a source dir automatically, unlike per-target KSP.
			kotlin.srcDir(layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))
		}

		commonTest.dependencies {
			implementation(project(":common-tests"))
		}

		jvmTest.dependencies {
			implementation(libs.joml)
		}
	}
}

dependencies {
	add("kspCommonMainMetadata", project(":kore-ksp"))
}

// The common-metadata srcDir above isn't a tracked task output, so consumers must depend on the KSP task filling it.
tasks.matching {
	it.name != "kspCommonMainKotlinMetadata" &&
		(it.name.startsWith("compileKotlin") ||
			it.name == "compileCommonMainKotlinMetadata" ||
			it.name.startsWith("ksp") ||
			it.name.contains("SourcesJar", ignoreCase = true))
}.configureEach {
	dependsOn("kspCommonMainKotlinMetadata")
}
