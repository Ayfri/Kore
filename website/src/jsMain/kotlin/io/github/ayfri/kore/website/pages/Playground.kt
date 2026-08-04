package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.setDescription
import io.github.ayfri.kore.website.components.common.setKeywords
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.components.playground.MonacoEditor
import io.github.ayfri.kore.website.components.playground.defaultExample
import io.github.ayfri.kore.website.externals.monaco.CodeEditor
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.paddingX
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Page
@Composable
fun PlaygroundPage() {
	Style(PlaygroundStyle)

	setDescription("Write Kotlin, get a Minecraft datapack. Try the Kore DSL in your browser, no install needed.")
	setKeywords(
		"kore playground", "kotlin datapack editor", "minecraft datapack generator online",
		"try kore", "datapack builder", "kotlin dsl playground"
	)

	var editorInstance by remember { mutableStateOf<CodeEditor?>(null) }
	var code by remember { mutableStateOf(defaultExample.code) }

	PageLayout("Playground - Try Kore in your browser") {
		Div({ classes(PlaygroundStyle.container) }) {
			Div({ classes(PlaygroundStyle.hero) }) {
				H1({ classes(PlaygroundStyle.pageTitle) }) {
					Text("Playground")
				}

				P({ classes(PlaygroundStyle.description) }) {
					Text("Write Kore Kotlin, run it, and get the generated datapack back. Nothing to install.")
				}
			}

			Div({ classes(PlaygroundStyle.workspace) }) {
				Div({ classes(PlaygroundStyle.pane) }) {
					Div({ classes(PlaygroundStyle.paneHeader) }) {
						Text("main.kt")
					}

					MonacoEditor(
						initialValue = defaultExample.code,
						className = PlaygroundStyle.editor,
						onChange = { code = it },
						onReady = { editorInstance = it },
					)
				}

				Div({ classes(PlaygroundStyle.pane) }) {
					Div({ classes(PlaygroundStyle.paneHeader) }) {
						Text("Output")
					}

					Div({ classes(PlaygroundStyle.placeholder) }) {
						Text("Run the code to see the generated datapack here.")
					}
				}
			}
		}
	}
}

object PlaygroundStyle : StyleSheet() {
	private val paneGap = 1.cssRem

	val container by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		paddingX(5.percent)
		paddingBottom(2.cssRem)
		paddingTop(1.cssRem)
		maxWidth(1600.px)
		marginX(auto)
		width(100.percent)

		mdMax(self) {
			paddingX(3.percent)
		}
	}

	val hero by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(0.5.cssRem)
		marginBottom(1.2.cssRem)
	}

	val pageTitle by style {
		fontSize(2.25.cssRem)
		fontWeight(700)
		textAlign(TextAlign.Center)
	}

	val description by style {
		fontSize(1.15.cssRem)
		color(GlobalStyle.altTextColor)
		textAlign(TextAlign.Center)
		maxWidth(760.px)
		lineHeight(1.5.number)
	}

	val workspace by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("minmax(0, 1fr) minmax(0, 1fr)")
		gap(paneGap)
		height(70.vh)
		minHeight(460.px)

		mdMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
			height(auto)
		}
	}

	val pane by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderRadius(GlobalStyle.roundingSection)
		border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		overflow(Overflow.Hidden)
		minHeight(0.px)

		mdMax(self) {
			height(60.vh)
		}
	}

	val paneHeader by style {
		padding(0.6.cssRem, 1.cssRem)
		fontSize(0.85.cssRem)
		color(GlobalStyle.altTextColor)
		borderBottom(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		flexShrink(0)
	}

	val editor by style {
		flexGrow(1)
		minHeight(0.px)
		width(100.percent)
	}

	val placeholder by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		flexGrow(1)
		color(GlobalStyle.altTextColor)
		fontSize(0.95.cssRem)
		padding(1.cssRem)
		textAlign(TextAlign.Center)
	}
}
