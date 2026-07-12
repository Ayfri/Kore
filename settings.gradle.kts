pluginManagement {
	includeBuild("build-logic")

	val versionsToml = file("gradle/libs.versions.toml").readLines()

	val kotlinVersion = versionsToml
		.first { it.startsWith("kotlin =") }
		.substringAfter("\"").substringBefore("\"")

	val kspVersion = versionsToml
		.first { it.startsWith("ksp =") }
		.substringAfter("\"").substringBefore("\"")


	plugins {
		kotlin("jvm") version kotlinVersion
		kotlin("multiplatform") version kotlinVersion
		kotlin("plugin.serialization") version kotlinVersion
		kotlin("plugin.compose") version kotlinVersion
		id("com.google.devtools.ksp") version kspVersion
	}

	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

rootProject.name = "Kore"

include(":common-tests")
include(":generation")
include(":kore-ksp")
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
