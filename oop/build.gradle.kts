import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	`publish-conventions`
}

repositories {
	mavenCentral()
}

dependencies {
	api(project(":kore"))
	implementation(libs.kotlinx.serialization)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xcontext-receivers")
	}
}

var runUnitTests = tasks.register<JavaExec>("runUnitTests") {
	description = "Runs the unit tests."
	group = "verification"

	classpath = sourceSets.test.get().runtimeClasspath
	mainClass = "MainKt"
	shouldRunAfter("test")
}

tasks.test {
	dependsOn(runUnitTests)
}

metadata {
	name = "OOP"
	description = "An OOP form of Kore, experimental and not complete."
}
