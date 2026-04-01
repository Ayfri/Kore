pluginManagement {
	includeBuild("build-logic")

	val kotlinVersion = file("gradle/libs.versions.toml").readLines()
		.first { it.startsWith("kotlin =") || it.startsWith("kotlin=") }
		.substringAfter("\"").substringBefore("\"")

	plugins {
		kotlin("jvm") version kotlinVersion
		kotlin("multiplatform") version kotlinVersion
		kotlin("plugin.serialization") version kotlinVersion
		kotlin("plugin.compose") version kotlinVersion
	}

	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

rootProject.name = "Kore"

include(":generation")
include(":kore")
include(":helpers")
include(":oop")
include(":bindings")
include(":website")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		google()
	}
}
