plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	`publish-conventions`
}

metadata {
	name = "Kore"
	description = "A Kotlin DSL to create Minecraft datapacks."
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.kotlinx.io)
	implementation(libs.kotlinx.serialization)
	implementation(libs.ktoml)
	implementation(kotlin("reflect"))
	api(libs.knbt)
	api(libs.joml)

	testImplementation(libs.kotlin.dotenv)
}

kotlin {
	jvmToolchain(17)

	compilerOptions {
		freeCompilerArgs =
			listOf("-Xcontext-parameters", "-Xrender-internal-diagnostic-names")
	}
}

tasks.compileKotlin {
	val generatedFolder = file("src/main/kotlin/io/github/ayfri/kore/generated")
	if (!generatedFolder.exists()) {
		dependsOn(":generation:run")
	}
}

var runUnitTests = tasks.register<JavaExec>("runUnitTests") {
	description = "Runs the unit tests."
	group = "verification"

	classpath = sourceSets.test.get().runtimeClasspath
	mainClass = "${Project.GROUP}.MainKt"
	shouldRunAfter("test")
}

tasks.test {
	dependsOn(runUnitTests)
}
