rootProject.name = "DatapackDSL"

include(":datapackDSL")
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
