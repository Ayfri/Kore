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
	implementation(libs.kotlinx.serialization)
	implementation(kotlin("reflect"))
	api(libs.knbt)
	api(libs.joml)

	testImplementation(libs.kotlin.dotenv)
}

kotlin {
	jvmToolchain(17)
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
