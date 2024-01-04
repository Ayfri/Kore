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
		}
	}

	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		google()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
		maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
	}

	versionCatalogs {
		create("libs") {
			from(files("libs.versions.toml"))
		}
	}
}
