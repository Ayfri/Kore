import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link
import kotlinx.html.script

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
			head.apply {
				add {
					link("/prism.min.css", "stylesheet")
					script("text/javascript", "/prism.min.js") {
						attributes += "data-manual" to ""
					}
				}
			}
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

kotlin {
	configAsKobwebApplication("website")

	js(IR) {
		browser {
			commonWebpackConfig {
				devServer?.open = false
			}
		}

		binaries.executable()
	}

	sourceSets {
		jsMain {
			dependencies {
				implementation(compose.html.core)
				implementation(libs.kobweb.core)
				implementation(compose.runtime)
				implementation(libs.kobwebx.markdown)
				implementation(libs.kobwebx.silk.icons.mdi)
			}
		}
	}
}
