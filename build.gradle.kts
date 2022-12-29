plugins {
    kotlin("jvm") version "1.8.0"
}

group = "fr.ayfri"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(18)
}
