rootProject.name = "Kore"

include(":kore")
include(":oop")
include(":generation")


pluginManagement {
	resolutionStrategy {
		plugins {
			val kotlinVersion = extra["kotlin.version"] as String
			kotlin("jvm") version kotlinVersion
			kotlin("plugin.serialization") version kotlinVersion
		}
	}

	repositories {
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			from(files("libs.versions.toml"))
		}
	}
}
