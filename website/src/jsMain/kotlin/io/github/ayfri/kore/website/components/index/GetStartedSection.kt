package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.AppGlobals
import io.github.ayfri.kore.website.components.common.BrandIcon
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.common.Markdown
import io.github.ayfri.kore.website.components.features.FeatureSectionsStyle
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
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
		classes(FeatureSectionsStyle.section)
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
				InstallationMethod.entries.forEach { entry ->
					Button({
						classes(HeroSectionStyle.tab)
						if (entry == method) classes(HeroSectionStyle.tabActive)
						onClick { method = entry }
					}) { Text(entry.label) }
				}
			}

			Div({
				id(INSTALL_CODE_ID)
				classes(GetStartedSectionStyle.installCode)
			}) {
				key(method) {
					Span(method.file, HeroSectionStyle.paneTitle)
					CodeBlock(method.code(version), method.language)
				}
			}
		}

		Div({ classes(GetStartedSectionStyle.tools) }) {
			Span("Works with", GetStartedSectionStyle.toolsLabel)
			tools.forEach { tool ->
				DomA(tool.link, {
					classes(GetStartedSectionStyle.tool)
					if (tool.link.startsWith("http")) target(ATarget.Blank)
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
		property("counter-reset", "step")
		property("list-style", "none")

		"li" style {
			borderTop(1.px, LineStyle.Solid, Color("var(--landing-border)"))
			paddingTop(1.3.cssRem)
			property("counter-increment", "step")
		}

		"li::before" style {
			color(Color("var(--landing-accent)"))
			display(DisplayStyle.Block)
			fontFamily("JetBrains Mono", "monospace")
			fontSize(0.85.cssRem)
			marginBottom(0.6.cssRem)
			property("content", "'0' counter(step)")
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
		color(Color("var(--landing-muted)"))
		fontSize(0.98.cssRem)
		lineHeight(1.6.number)
		margin(0.5.cssRem, 0.px, 0.px)

		"p" style { margin(0.px) }
	}

	val install by style {
		backgroundColor(Color("var(--landing-surface-2)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.1.cssRem)
		marginX(auto)
		maxWidth(52.cssRem)
		overflow(Overflow.Hidden)
	}

	val installBar by style {
		borderBottom(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.3.cssRem)
		padding(0.5.cssRem)
	}

	val installCode by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)

		"div.code-toolbar > .toolbar" style { display(DisplayStyle.None) }

		"div.code-toolbar" style {
			backgroundColor(Color.transparent)
			border(0.px)
			borderRadius(0.px)
		}

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
		color(Color("var(--landing-muted)"))
		fontSize(0.9.cssRem)
	}

	val tool by style {
		alignItems(AlignItems.Center)
		color(Color("var(--landing-text)"))
		display(DisplayStyle.Flex)
		fontSize(0.92.cssRem)
		fontWeight(500)
		gap(0.5.cssRem)
		transition(0.2.s, "color")

		hover(self) style {
			color(Color("var(--landing-accent-strong)"))
		}
	}
}
