import java.util.*

plugins {
	`kotlin-dsl`
	kotlin("jvm") version embeddedKotlinVersion
}

repositories {
	mavenCentral()
	gradlePluginPortal()
}

val props = Properties().apply {
	file("../gradle.properties").inputStream().use { load(it) }
}

fun version(target: String): String = props.getProperty("$target.version")

dependencies {
	implementation(kotlin("gradle-plugin", version("kotlin")))
	implementation(kotlin("script-runtime"))
	implementation(kotlin("stdlib-jdk8"))
	implementation("org.jreleaser:jreleaser-gradle-plugin:1.19.0")
}

kotlin {
	jvmToolchain(17)
}
