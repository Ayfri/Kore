plugins {
	kotlin("jvm")
	application
}


repositories {
	mavenCentral()
}

dependencies {
	val ktorVersion = "2.2.1"
	implementation("io.ktor:ktor-client-core:$ktorVersion")
	implementation("io.ktor:ktor-client-cio:$ktorVersion")
	implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-serialization:$ktorVersion")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

	implementation("com.squareup:kotlinpoet:1.12.0")
}

kotlin {
	jvmToolchain(18)
}

application {
	mainClass.set("MainKt")
}
