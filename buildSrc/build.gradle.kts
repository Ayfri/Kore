val kotlinVersion = "2.0.10"

plugins {
	`kotlin-dsl`
	kotlin("jvm") version embeddedKotlinVersion
}

repositories {
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	implementation(kotlin("gradle-plugin", kotlinVersion))
	implementation(kotlin("script-runtime"))
	implementation(kotlin("stdlib-jdk8"))
}

kotlin {
	jvmToolchain(17)
}
