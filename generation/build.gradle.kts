plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	application
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.ktor.client.core)
	implementation(libs.ktor.client.cio)
	implementation(libs.ktor.client.content.negotiation)
	implementation(libs.ktor.serialization)
	implementation(libs.ktor.serialization.kotlinx.json)

	implementation(libs.kotlinpoet)
}

kotlin {
	jvmToolchain(21)
}

application {
	mainClass = "MainKt"
}
