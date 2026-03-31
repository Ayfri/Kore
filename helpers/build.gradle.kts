plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	`publish-conventions`
}

metadata {
	name = "Helpers"
	description = "Helper utilities built on top of Kore and its OOP abstractions."
}

repositories {
	mavenCentral()
}

dependencies {
	api(project(":oop"))
	implementation(libs.kotlinx.io)
	implementation(libs.kotlinx.serialization)
}

kotlin {
	jvmToolchain(21)

	compilerOptions {
		freeCompilerArgs = listOf("-Xcontext-parameters")
	}
}

var runUnitTests = tasks.register<JavaExec>("runUnitTests") {
	description = "Runs the unit tests."
	group = "verification"

	classpath = sourceSets.test.get().runtimeClasspath
	mainClass = "io.github.ayfri.kore.helpers.MainKt"
	shouldRunAfter("test")
}

tasks.test {
	dependsOn(runUnitTests)
}