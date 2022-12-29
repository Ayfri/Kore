plugins {
	kotlin("jvm") version "1.8.0"
	kotlin("plugin.serialization") version "1.8.0"
	application
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
	implementation(kotlin("reflect"))
	api("net.benwoodworth.knbt:knbt:0.11.3")
}

application {
	mainClass.set("MainKt")
}
