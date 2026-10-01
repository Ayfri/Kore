package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.AppGlobals
import io.github.ayfri.kore.website.CodeThemeStyle
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.*
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.A as DomA

private enum class InstallationMethod(val label: String, val file: String, val language: String) {
	GRADLE_KOTLIN("Gradle (Kotlin)", "build.gradle.kts", "kotlin") {
		override fun code(version: String) = """
			plugins {
			    // Optional: build, link and reload your pack from Gradle
			    id("io.github.ayfri.kore") version "$version"
			}

			dependencies {
			    implementation("io.github.ayfri.kore:kore:$version")
			}
		""".trimIndent()
	},
	GRADLE_GROOVY("Gradle (Groovy)", "build.gradle", "groovy") {
		override fun code(version: String) = """
			plugins {
			    // Optional: build, link and reload your pack from Gradle
			    id 'io.github.ayfri.kore' version '$version'
			}

			dependencies {
			    implementation 'io.github.ayfri.kore:kore:$version'
			}
		""".trimIndent()
	},
	MAVEN("Maven", "pom.xml", "xml") {
		override fun code(version: String) = """
			<dependency>
			    <groupId>io.github.ayfri.kore</groupId>
			    <artifactId>kore</artifactId>
			    <version>$version</version>
			</dependency>
		""".trimIndent()
	},
	KOTLIN_TOOLCHAIN("Kotlin Toolchain", "module.yaml", "yaml") {
		override fun code(version: String) = """
			dependencies:
			  - io.github.ayfri.kore:kore:$version
		""".trimIndent()
	};

	abstract fun code(version: String): String
}

private class Tool(val name: String, val icon: String, val link: String)

private val setupSteps = listOf(
	"Clone the template" to "The [Kore Template](https://github.com/Kore-Minecraft/Kore-Template) is a ready-to-run Gradle project. Already have one? Add the dependency below.",
	"Describe your pack" to "Open a `dataPack(\"my_pack\") { }` block in `main` and let autocomplete show you what fits inside.",
	"Generate and play" to "Run `main` and load the pack in your world. The Gradle plugin can rebuild and reload it on every save.",
)

private val tools = listOf(
	Tool("IntelliJ IDEA plugin", "intellijidea", "https://plugins.jetbrains.com/plugin/27025-kore-assistant"),
	Tool("VS Code extension", "visualstudiocode", "https://marketplace.visualstudio.com/items?itemName=ayfri.kore-assistant"),
	Tool("Gradle plugin", "gradle", "/docs/guides/gradle-plugin"),
)

private const val INSTALL_CODE_ID = "install-code"

@Composable
fun GetStartedSection() {
	Style(GetStartedSectionStyle)

	var method by remember { mutableStateOf(InstallationMethod.GRADLE_KOTLIN) }
	val version = AppGlobals.getValue("projectVersion") + "-" + AppGlobals.getValue("minecraftVersion")
	highlightCodeIn(INSTALL_CODE_ID, method)

	Section({
		id("get-started")
		classes(HomeSectionStyle.section)
	}) {
		SectionHeader(
			"Up and running in a few minutes",
			"All you need is a JDK and a Kotlin IDE. Kore is a regular dependency on Maven Central, published for every Minecraft version it supports.",
		)

		Ol({ classes(GetStartedSectionStyle.steps) }) {
			setupSteps.forEach { (title, description) ->
				Li {
					H3 { Text(title) }
					Markdown(description, GetStartedSectionStyle.stepText)
				}
			}
		}

		Div({ classes(GetStartedSectionStyle.install) }) {
			Div({ classes(GetStartedSectionStyle.installBar) }) {
				Segmented(InstallationMethod.entries, method, { method = it }, { it.label }, SegmentedStyle.flat)
			}

			Div({
				id(INSTALL_CODE_ID)
				classes(CodeThemeStyle.bare, GetStartedSectionStyle.installCode)
			}) {
				key(method) {
					Span(method.file, GetStartedSectionStyle.fileName)
					CodeBlock(method.code(version), method.language)
				}
			}
		}

		Div({ classes(GetStartedSectionStyle.tools) }) {
			Span("Works with", GetStartedSectionStyle.toolsLabel)
			tools.forEach { tool ->
				DomA(tool.link, {
					classes(GetStartedSectionStyle.tool)
					externalTarget(tool.link)
				}) {
					BrandIcon(tool.icon)
					Text(tool.name)
				}
			}
		}
	}
}

object GetStartedSectionStyle : StyleSheet() {
	val steps by style {
		display(DisplayStyle.Grid)
		gap(2.5.cssRem)
		gridTemplateColumns("repeat(3, minmax(0, 1fr))")
		margin(0.px, 0.px, 2.5.cssRem)
		padding(0.px)
		counterReset("step")
		listStyle(ListStyle.None)

		"li" style {
			borderTop(1.px, LineStyle.Solid, LandingVars.Border.value())
			paddingTop(1.3.cssRem)
			counterIncrement("step")
		}

		"li::before" style {
			color(LandingVars.Accent.value())
			display(DisplayStyle.Block)
			fontSize(0.85.cssRem)
			marginBottom(0.6.cssRem)
			monoFont()
			content(Content.list(Content.of("0"), counter("step")))
		}

		"h3" style {
			fontSize(1.15.cssRem)
			margin(0.px)
		}

		mdMax(self) {
			gap(1.8.cssRem)
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val stepText by style {
		color(LandingVars.Muted.value())
		fontSize(0.98.cssRem)
		lineHeight(1.6)
		margin(0.5.cssRem, 0.px, 0.px)

		"p" style { margin(0.px) }
	}

	val install by style {
		backgroundColor(LandingVars.Pane.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(1.1.cssRem)
		marginX(auto)
		maxWidth(52.cssRem)
		overflow(Overflow.Hidden)
	}

	val installBar by style {
		borderBottom(1.px, LineStyle.Solid, LandingVars.Border.value())
		padding(0.5.cssRem)
	}

	val fileName by style {
		borderBottom(1.px, LineStyle.Solid, LandingVars.Border.value())
		color(LandingVars.Muted.value())
		fontSize(0.82.cssRem)
		padding(0.7.cssRem, 1.1.cssRem)
	}

	val installCode by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)

		"pre" style {
			backgroundColor(Color.transparent)
			fontSize(0.85.cssRem)
			margin(0.px)
			overflowX(Overflow.Auto)
		}
	}

	val tools by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.6.cssRem, 1.6.cssRem)
		justifyContent(JustifyContent.Center)
		marginTop(2.cssRem)
	}

	val toolsLabel by style {
		color(LandingVars.Muted.value())
		fontSize(0.9.cssRem)
	}

	val tool by style {
		alignItems(AlignItems.Center)
		color(LandingVars.Text.value())
		display(DisplayStyle.Flex)
		fontSize(0.92.cssRem)
		fontWeight(500)
		gap(0.5.cssRem)
		transition(0.2.s, "color")

		hover(self) style {
			color(LandingVars.AccentStrong.value())
		}
	}
}
