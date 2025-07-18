plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	`publish-conventions`
}

metadata {
	name = "OOP"
	description = "An OOP form of Kore, experimental and not complete."
}

repositories {
	mavenCentral()
}

dependencies {
	api(project(":kore"))
	implementation(libs.kotlinx.serialization)
}

kotlin {
	jvmToolchain(17)

	compilerOptions {
		freeCompilerArgs = listOf("-Xcontext-parameters")
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
