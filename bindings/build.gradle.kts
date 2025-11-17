plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
}

repositories {
	mavenCentral()
}

kotlin {
	jvmToolchain(17)

	compilerOptions {
		freeCompilerArgs =
			listOf("-Xcontext-parameters", "-Xrender-internal-diagnostic-names")
	}
}

dependencies {
	implementation(libs.kotlinpoet)
	testImplementation(libs.kotlinx.io)
	implementation(libs.kotlinx.serialization)
	implementation(project(":kore"))
}


var runUnitTests = tasks.register<JavaExec>("runUnitTests") {
	description = "Runs the unit tests."
	group = "verification"

	classpath = sourceSets.test.get().runtimeClasspath
	mainClass = "${Project.GROUP}.bindings.MainKt"
	shouldRunAfter("test")
}

tasks.test {
	dependsOn(runUnitTests)
}
