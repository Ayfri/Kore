rootProject.name = "Kore"

include(":kore")
include(":oop")
include(":generation")
include(":website")


pluginManagement {
	resolutionStrategy {
		plugins {
			val kotlinVersion = extra["kotlin.version"] as String
			kotlin("jvm") version kotlinVersion
			kotlin("multiplatform") version kotlinVersion
			kotlin("plugin.serialization") version kotlinVersion
			kotlin("plugin.compose") version kotlinVersion
		}
	}

	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		google()
		maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
	}
}
