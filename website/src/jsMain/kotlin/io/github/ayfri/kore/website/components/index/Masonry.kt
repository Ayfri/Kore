package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.utils.P
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.marginY
import org.jetbrains.compose.web.css.*
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

		style {
			marginTop(3.cssRem)
		}
	}) {
		rightItems.forEach { MasonryItemCard(it) }
	}
}

@Composable
private fun MasonryItemCard(item: MasonryItem) = Div(attrs = {
	classes(MasonryStyle.card)
}) {
	CodeBlock(item.code, "kotlin")

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
		marginY(3.cssRem)
	}

	val column by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.5.cssRem)
		width(40.percent)
	}

	val card by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingSection)
		padding(1.cssRem)
	}

	val cardContent by style {
		marginX(1.cssRem)
	}
}
