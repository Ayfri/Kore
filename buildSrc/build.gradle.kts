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

fun version(target: String) = props.getProperty("$target.version")

dependencies {
	implementation(kotlin("gradle-plugin", version("kotlin")))
	implementation(kotlin("script-runtime"))
	implementation(kotlin("stdlib-jdk8"))
}
kotlin {
	jvmToolchain(17)
}
