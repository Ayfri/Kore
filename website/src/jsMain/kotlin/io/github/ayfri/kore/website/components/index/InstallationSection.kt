package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.fontWeight
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.core.AppGlobals
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.Button
import io.github.ayfri.kore.website.components.common.ButtonColor
import io.github.ayfri.kore.website.components.common.ButtonVariant
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
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

		val version = AppGlobals.getValue("projectVersion") + "-" + AppGlobals.getValue("minecraftVersion")
		key(selectedMethod) {
			LaunchedEffect(selectedMethod) {
				js("Prism.highlightAll()")
			}
			CodeBlock(selectedMethod.getCode(version), selectedMethod.language, InstallationSectionStyle.code)
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
}

object InstallationSectionStyle : StyleSheet() {
	val container by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		textAlign(TextAlign.Center)
		paddingY(2.5.cssRem)
		paddingX(2.cssRem)
		property("margin-left", auto)
		property("margin-right", auto)
		maxWidth(80.percent)

		"h2" style {
			fontSize(2.2.cssRem)
			marginBottom(1.5.cssRem)
			fontWeight(FontWeight.Bold)
		}

		mdMax(self) {
			maxWidth(95.percent)
			paddingY(2.cssRem)
			"h2" style {
				fontSize(1.8.cssRem)
			}
		}
	}

	val description by style {
		color(GlobalStyle.altTextColor)
		fontSize(1.1.cssRem)
		marginBottom(3.cssRem)
		maxWidth(45.cssRem)
		property("line-height", "1.6")
	}

	val selector by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(1.cssRem)
		marginBottom(2.cssRem)
		flexWrap(FlexWrap.Wrap)
		justifyContent(JustifyContent.Center)
	}

	val code by style {
		borderRadius(GlobalStyle.roundingSection)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		border(1.px, LineStyle.Solid, GlobalStyle.borderColor.alpha(0.1))
		property("box-shadow", "0 10px 30px ${GlobalStyle.shadowColor.alpha(0.3)}")
		width(40.cssRem)
	}

	val info by style {
		marginTop(2.5.cssRem)
		color(GlobalStyle.altTextColor)
		fontSize(1.1.cssRem)

		"a" style {
			color(GlobalStyle.logoRightColor)
			fontWeight(FontWeight.Bold)
			transition(0.3.s, "color")

			hover(self) style {
				color(GlobalStyle.logoRightColor.alpha(0.8))
			}
		}
	}
}
