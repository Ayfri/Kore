plugins {
	kotlin("jvm")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.ksp.symbol.processing.api)
}

kotlin {
	jvmToolchain(25)
}
