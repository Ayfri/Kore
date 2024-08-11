plugins {
	`kotlin-dsl`
	kotlin("jvm") version embeddedKotlinVersion
}

repositories {
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	implementation(kotlin("gradle-plugin", embeddedKotlinVersion))
	implementation(kotlin("script-runtime"))
	implementation(kotlin("stdlib-jdk8"))
}

kotlin {
	jvmToolchain(17)
}
