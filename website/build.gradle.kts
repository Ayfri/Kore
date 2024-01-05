import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link

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

			// <link rel="preconnect" href="https://fonts.googleapis.com">
			//<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
			//<link href="https://fonts.googleapis.com/css2?family=Roboto:wght@100;300;500;900&display=swap" rel="stylesheet">

			head.add {
				link("https://fonts.googleapis.com", "preconnect")
				link("https://fonts.gstatic.com", "preconnect") {
					attributes["crossorigin"] = "anonymous"
				}
				link("https://fonts.googleapis.com/css2?family=Roboto:wght@100;300;500;900&display=swap", "stylesheet")
			}
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
