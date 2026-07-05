plugins {
	kotlin("multiplatform")
	kotlin("plugin.serialization")
	alias(libs.plugins.ksp)
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

kotlin {
	jvm()
	js {
		browser()
		nodejs()
	}
	jvmToolchain(25)

	compilerOptions {
		freeCompilerArgs.addAll(listOf("-Xrender-internal-diagnostic-names"))
	}

	sourceSets {
		commonMain {
			dependencies {
				implementation(libs.kotlinx.io)
				implementation(libs.kotlinx.serialization)
				api(libs.knbt)
			}
			kotlin.srcDir(layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))
		}

		jvmMain.dependencies {
			implementation(libs.ktoml)
		}

		jvmTest.dependencies {
			implementation(libs.kotlin.dotenv)
		}
	}
}

dependencies {
	add("kspCommonMainMetadata", project(":kore-ksp"))
	add("kspJvmTest", project(":kore-ksp"))
}

tasks.named("compileKotlinJvm") {
	val generatedFolder = file("src/commonMain/kotlin/io/github/ayfri/kore/generated")
	if (!generatedFolder.exists()) {
		dependsOn(":generation:run")
	}
}
