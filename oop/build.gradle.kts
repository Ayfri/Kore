import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.8.0"
	application
}

repositories {
	mavenCentral()
}

dependencies {
	api(project(":datapackDSL"))
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xcontext-receivers")
	}
}

application {
	mainClass.set("MainKt")
}
