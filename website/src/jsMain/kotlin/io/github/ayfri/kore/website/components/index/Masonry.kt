package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.Nth
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text

data class MasonryItem(
	val title: String,
	val description: String,
	val code: String,
)

@Composable
fun Masonry(items: List<MasonryItem>) = Div({
	classes(MasonryStyle.container)
}) {
	Style(MasonryStyle)

	val leftItems = items.filterIndexed { index, _ -> index % 2 == 0 }
	val rightItems = items.filterIndexed { index, _ -> index % 2 != 0 }

	Div({
		classes(MasonryStyle.column)
	}) {
		leftItems.forEach { MasonryItemCard(it) }
	}

	Div({
		classes(MasonryStyle.column)
	}) {
		rightItems.forEach { MasonryItemCard(it) }
	}
}

@Composable
private fun MasonryItemCard(item: MasonryItem) = Div(attrs = {
	classes(MasonryStyle.card)
}) {
	CodeBlock(item.code, "kotlin", MasonryStyle.code)

	Div({
		classes(MasonryStyle.cardContent)
	}) {
		H2 {
			Text(item.title)
		}
		P(item.description)
	}
}

object MasonryStyle : StyleSheet() {
	val container by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		justifyContent(JustifyContent.Center)
		gap(2.cssRem)
		marginX(1.cssRem)
		marginY(2.cssRem)

		lgMax(self) {
			alignItems(AlignItems.Center)
			flexDirection(FlexDirection.Column)
			gap(3.5.cssRem)
			marginY(0.cssRem)
		}
	}

	val column by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(2.cssRem)
		width(40.percent)

		self + nthOfType(Nth.Even) style {
			marginTop(3.cssRem)

			lgMax(child(className(container), self)) {
				marginTop(0.cssRem)
			}
		}

		xlMax(self) {
			width(47.5.percent)
		}

		lgMax(self) {
			gap(3.5.cssRem)
			width(85.percent)
		}

		xsMax(self) {
			width(100.percent)
		}
	}

	val card by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingSection)
		padding(1.cssRem)
	}

	val cardContent by style {
		marginX(1.cssRem)
	}

	val code by style {
		borderRadius(GlobalStyle.roundingButton)
	}
}
