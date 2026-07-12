plugins {
	kotlin("multiplatform")
	kotlin("plugin.serialization")
	alias(libs.plugins.ksp)
	alias(libs.plugins.kotest)
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

		commonTest.dependencies {
			implementation(project(":common-tests"))
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

// Bootstrap generated MC enums/registries on a fresh checkout, for every target that needs them.
if (!file("src/commonMain/kotlin/io/github/ayfri/kore/generated").exists()) {
	tasks.matching {
		it.name.startsWith("compileKotlin") ||
			it.name == "compileCommonMainKotlinMetadata" ||
			it.name.contains("SourcesJar", ignoreCase = true)
	}.configureEach {
		dependsOn(":generation:run")
	}
}

// The common-metadata srcDir above isn't a tracked task output, so consumers must depend on the KSP task filling it.
tasks.matching {
	it.name != "kspCommonMainKotlinMetadata" &&
		(it.name.startsWith("compileKotlin") ||
			it.name == "compileCommonMainKotlinMetadata" ||
			it.name.contains("SourcesJar", ignoreCase = true))
}.configureEach {
	dependsOn("kspCommonMainKotlinMetadata")
}
