package io.github.ayfri.kore.website

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.selectors.CSSSelector

object CodeThemeStyle : StyleSheet() {
	val textColor = Color.white
	val commentColor = Color("#757575")
	val numberColor = Color("#f78c6c")
	val stringColor = Color("#c3e88d")
	val keywordColor = Color("#c792ea")
	val functionColor = Color("#82aaff")
	val classColor = Color("#ffcb6b")
	val punctuationColor = Color("#89ddff")

	fun scope(vararg names: String): CSSSelector {
		val initial = className("token")
		val subClasses = names.map { initial + className(it) }
		return group(*subClasses.toTypedArray())
	}

	init {
		val toolbarSelector = child(type("div") + className("code-toolbar"), className("toolbar"))
		(type("div") + className("code-toolbar")) style {
			position(Position.Relative)
		}

		toolbarSelector style {
			display(DisplayStyle.Flex)
			gap(0.5.cssRem)
			alignItems(AlignItems.Center)
			position(Position.Absolute)
			top(0.85.cssRem)
			right(0.85.cssRem)
			zIndex(1)

			fun buttonType(type: String) = child(toolbarSelector, child(className("toolbar-item"), type(type)))

			group(buttonType("button"), buttonType("a"), buttonType("span")) style {
				backgroundColor(Color("#333339"))
				borderRadius(0.6.cssRem)
				cursor(Cursor.Default)
				padding(0.35.cssRem, 0.75.cssRem)
			}

			buttonType("button") style {
				cursor(Cursor.Pointer)
			}
		}

		val codeSelector = child(type("div") + className("code-toolbar"), type("pre"))

		codeSelector style {
			backgroundColor(GlobalStyle.secondaryBackgroundColor)
			margin(0.px)
		}

		desc(className("line-numbers"), className("line-numbers-rows")) style {
			left((-2.75).cssRem)
			width(1.75.cssRem)
		}

		child(type("pre") + attrContains("class", "language-"), type("code") + attrContains("class", "language-")) style {
			color(textColor)
		}

		scope("comment") style {
			color(commentColor)
		}

		scope("number") style {
			color(numberColor)
		}

		scope("string", "char", "regex") style {
			color(stringColor)
		}

		scope("keyword", "annotation.builtin") style {
			color(keywordColor)
			fontStyle(FontStyle.Italic)
		}

		scope("punctuation", "operator") style {
			color(punctuationColor)
		}

		scope("function") style {
			color(functionColor)
		}

		scope("class-name", "builtin") style {
			color(classColor)
		}
	}
}
