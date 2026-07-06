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
		freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
	}

	sourceSets {
		commonMain {
			dependencies {
				implementation(libs.kotlinx.io)
				implementation(libs.kotlinx.serialization)
				api(libs.knbt)
			}
			// KSP doesn't expose common-metadata output as a source dir automatically, unlike per-target KSP.
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
	add("kspJsTest", project(":kore-ksp"))
}

// Bootstrap the generated MC enums/registries on a fresh checkout before the first JVM compile.
tasks.named("compileKotlinJvm") {
	if (!file("src/commonMain/kotlin/io/github/ayfri/kore/generated").exists()) {
		dependsOn(":generation:run")
	}
}

// The common-metadata srcDir added above isn't tracked as a task output, so every consumer must
// depend on the KSP task that fills it, or builds race. Per-target KSP tasks wire themselves.
tasks.matching {
	it.name != "kspCommonMainKotlinMetadata" &&
		(it.name.startsWith("compileKotlin") || it.name.contains("SourcesJar", ignoreCase = true))
}.configureEach {
	dependsOn("kspCommonMainKotlinMetadata")
}
