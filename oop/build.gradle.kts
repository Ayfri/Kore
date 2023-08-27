import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	application
	`publish-conventions`
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
	mainClass = "MainKt"
}
