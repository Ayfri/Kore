plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	`publish-conventions`
}

metadata {
	name = "Kore Bindings"
	description = "Imports existing Minecraft datapacks and generates type-safe Kotlin bindings."
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
	implementation(libs.kotlinx.serialization)
	implementation(project(":kore"))

	testImplementation(libs.kotlin.dotenv)
	testImplementation(libs.kotlinx.io)
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
