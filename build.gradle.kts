plugins {
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
    application
}

group = "fr.ayfri"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation(kotlin("reflect"))
    implementation("net.benwoodworth.knbt:knbt:0.11.3")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

application {
    mainClass.set("MainKt")
}
