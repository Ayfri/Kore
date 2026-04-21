package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.AppGlobals
import io.github.ayfri.kore.website.components.common.Button
import io.github.ayfri.kore.website.components.common.ButtonColor
import io.github.ayfri.kore.website.components.common.ButtonVariant
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*

enum class InstallationMethod(val label: String, val language: String) {
	GRADLE_KOTLIN("Gradle (Kotlin)", "kotlin") {
		override fun getCode(version: String) = """
			dependencies {
			    implementation("io.github.ayfri.kore:kore:$version")
			}
		""".trimIndent()
	},
	GRADLE_GROOVY("Gradle (Groovy)", "groovy") {
		override fun getCode(version: String) = """
			dependencies {
			    implementation 'io.github.ayfri.kore:kore:$version'
			}
		""".trimIndent()
	},
	MAVEN("Maven", "xml") {
		override fun getCode(version: String) = """
			<dependency>
			    <groupId>io.github.ayfri.kore</groupId>
			    <artifactId>kore</artifactId>
			    <version>$version</version>
			</dependency>
		""".trimIndent()
	},
	AMPER("Amper", "yaml") {
		override fun getCode(version: String) = """
			dependencies:
			  - io.github.ayfri.kore:kore:$version
		""".trimIndent()
	};

	abstract fun getCode(version: String): String
}

@Composable
fun InstallationSection() {
	Style(InstallationSectionStyle)

	var selectedMethod by remember { mutableStateOf(InstallationMethod.GRADLE_KOTLIN) }

	Section({
		classes(InstallationSectionStyle.container)
	}) {
		Div({
			classes(InstallationSectionStyle.inner)
		}) {
			Div({
				classes(InstallationSectionStyle.copy)
			}) {
				H2 {
					Text("Quick Installation")
				}

				P({
					classes(InstallationSectionStyle.description)
				}) {
					Text("Start building your Minecraft datapacks with Kore by adding it to your project's dependencies.")
				}

				Div({
					classes(InstallationSectionStyle.selector)
				}) {
					InstallationMethod.entries.forEach { method ->
						Button(
							method.label,
							onClick = {
								selectedMethod = method
							},
							variant = if (selectedMethod == method) ButtonVariant.CONTAINED else ButtonVariant.OUTLINE,
							color = if (selectedMethod == method) ButtonColor.BRAND else ButtonColor.SECONDARY
						)
					}
				}

				P({
					classes(InstallationSectionStyle.info)
				}) {
					Text("Check out the ")
					A(href = "https://github.com/Ayfri/Kore/releases", content = "latest releases")
					Text(" or read the ")
					A(href = "/docs/getting-started", content = "getting started")
					Text(".")
				}
			}

			Div({
				classes(InstallationSectionStyle.panel)
			}) {
				val version = AppGlobals.getValue("projectVersion") + "-" + AppGlobals.getValue("minecraftVersion")
				key(selectedMethod) {
					LaunchedEffect(selectedMethod) {
						js("Prism.highlightAll()")
					}
					CodeBlock(selectedMethod.getCode(version), selectedMethod.language, InstallationSectionStyle.code)
				}
			}
		}
	}
}

object InstallationSectionStyle : StyleSheet() {
	val container by style {
		marginX(auto)
		marginTop(1.5.cssRem)
		marginBottom(3.cssRem)
		maxWidth(85.cssRem)
		property("box-sizing", "border-box")
		padding(2.2.cssRem, 2.75.cssRem)
		borderRadius(1.6.cssRem)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		property(
			"background",
			"linear-gradient(135deg, rgba(21, 28, 38, 0.95) 0%, rgba(15, 20, 27, 0.92) 55%, rgba(8, 182, 214, 0.1) 100%)"
		)
		property("box-shadow", "0 24px 70px rgba(5, 12, 20, 0.45)")

		"h2" style {
			fontSize(2.4.cssRem)
			marginBottom(1.25.cssRem)
			fontWeight(FontWeight.Bold)
		}

		mdMax(self) {
			maxWidth(92.percent)
			padding(2.cssRem, 2.cssRem)
			"h2" style {
				fontSize(2.cssRem)
			}
		}

		smMax(self) {
			maxWidth(100.percent)
			marginTop(1.cssRem)
			marginBottom(2.cssRem)
			padding(1.4.cssRem, 1.05.cssRem)
			borderRadius(1.25.cssRem)
		}
	}

	val inner by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("minmax(0, 1fr) minmax(0, 1fr)")
		alignItems(AlignItems.Center)
		gap(2.4.cssRem)
		minWidth(0.px)

		mdMax(self) {
			gridTemplateColumns("1fr")
			textAlign(TextAlign.Center)
		}
	}

	val copy by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.2.cssRem)
		minWidth(0.px)
	}

	val description by style {
		color(Color("var(--landing-muted)"))
		fontSize(1.1.cssRem)
		maxWidth(34.cssRem)

		mdMax(self) {
			maxWidth(100.percent)
			marginX(auto)
		}
	}

	val selector by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(0.9.cssRem)
		flexWrap(FlexWrap.Wrap)
		justifyContent(JustifyContent.FlexStart)

		mdMax(self) {
			justifyContent(JustifyContent.Center)
		}

		smMax(self) {
			alignItems(AlignItems.Stretch)
			flexDirection(FlexDirection.Column)
			width(100.percent)

			"button" style {
				justifyContent(JustifyContent.Center)
				width(100.percent)
			}
		}
	}

	val panel by style {
		backgroundColor(Color("var(--landing-surface-2)"))
		borderRadius(1.2.cssRem)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		minWidth(0.px)
		overflow(Overflow.Hidden)
		padding(1.5.cssRem)
		width(100.percent)

		smMax(self) {
			padding(1.cssRem)
		}
	}

	val code by style {
		borderRadius(1.cssRem)
		backgroundColor(Color("rgba(9, 15, 22, 0.95)"))
		border(1.px, LineStyle.Solid, Color("rgba(8, 182, 214, 0.2)"))
		property("box-shadow", "0 12px 35px rgba(5, 12, 20, 0.4)")
		maxWidth(100.percent)
		property("overflow-x", "auto")
		width(100.percent)

		smMax(self) {
			fontSize(0.82.cssRem)
		}
	}

	val info by style {
		color(Color("var(--landing-muted)"))
		fontSize(1.1.cssRem)

		"a" style {
			color(Color("var(--landing-accent)"))
			fontWeight(FontWeight.Bold)
			transition(0.3.s, "color")

			hover(self) style {
				color(Color("var(--landing-accent-strong)"))
			}
		}
	}
}
