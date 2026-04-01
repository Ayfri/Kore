plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("kotest-conventions")
	id("publish-conventions")
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
	jvmToolchain(21)

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
