import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
	kotlin("multiplatform")
	alias(libs.plugins.jetbrains.compose)
	alias(libs.plugins.kobweb.application)
	alias(libs.plugins.kobwebx.markdown)
}

group = "io.github.ayfri.kore.website"
version = "1.0-SNAPSHOT"

kobweb {
	app {
		index {
			description = "Kore website"
		}

		export {
			includeSourceMap = false
		}
	}
}

compose {
	val kotlinVersion = project.mainProjectProperty("kotlin.version")
	kotlinCompilerPlugin = dependencies.compiler.forKotlin("1.9.21")
	kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=${kotlinVersion}")
}

kotlin {
	configAsKobwebApplication("website")

	js(IR) {
		browser {
			commonWebpackConfig {
				devServer?.open = false
			}
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				implementation(compose.runtime)
			}
		}

		jsMain {
			dependencies {
				implementation(compose.html.core)
				implementation(libs.kobweb.core)
				implementation(libs.kobwebx.markdown)
			}
		}
	}
}
