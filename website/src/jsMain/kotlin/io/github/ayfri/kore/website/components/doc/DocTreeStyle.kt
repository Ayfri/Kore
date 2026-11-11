package io.github.ayfri.kore.website.components.doc

import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.lineHeight
import io.github.ayfri.kore.website.utils.marginY
import io.github.ayfri.kore.website.utils.monoFont
import io.github.ayfri.kore.website.utils.sansFont
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent

data object DocTreeStyle : StyleSheet() {
	val actionButton by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(0.px)
		color(GlobalStyle.altTextColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontSize(1.cssRem)
		justifyContent(JustifyContent.Center)
		opacity(0.7)
		padding(0.15.cssRem)
		transition(0.2.s, "color", "opacity")

		"svg" style {
			fontSize(1.1.cssRem)
		}

		self + hover style {
			color(GlobalStyle.linkColor)
			opacity(1)
		}
	}

	val actionIcon by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)

		child(self, type("svg")) style {
			height(1.05.cssRem)
			width(1.05.cssRem)
		}
	}

	val actions by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexShrink(0)
		gap(0.15.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val collapseToggle by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		height(0.9.cssRem)
		justifyContent(JustifyContent.Center)
		transform { rotate(90.deg) }
		transition(0.2.s, "transform")
		userSelect(UserSelect.None)
		width(0.9.cssRem)

		"svg" style {
			fontSize(0.9.cssRem)
		}
	}

	/** Declared after [collapseToggle] so its transform wins the cascade at equal specificity. */
	@OptIn(ExperimentalComposeWebApi::class)
	val collapsed by style {
		transform { rotate(0.deg) }
	}

	/** Takes the height the sidebar has left, so [list] scrolls inside it instead of the sidebar growing past the screen. */
	val container by style {
		display(DisplayStyle.Flex)
		flex(1)
		flexDirection(FlexDirection.Column)
		marginLeft(0.25.cssRem)
		minHeight(0.px)
	}

	val groupRow by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(0.px)
		color(GlobalStyle.altTextColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		sansFont()
		fontSize(0.9.cssRem)
		fontWeight(600)
		gap(0.45.cssRem)
		marginTop(0.35.cssRem)
		padding(0.25.cssRem, 0.3.cssRem)
		textAlign(TextAlign.Left)
		transition(0.2.s, "color")
		width(100.percent)

		self + hover style {
			color(GlobalStyle.textColor)
		}
	}

	val groupRowCurrent by style {
		color(GlobalStyle.textColor)
	}

	val header by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.5.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
	}

	val list by style {
		listStyle(ListStyle.None)
		marginY(0.2.cssRem)
		overflowY(Overflow.Auto)
		padding(0.4.cssRem)

		smMax(self) {
			marginBottom(0.px)
		}
	}

	val moduleTag by style {
		color(GlobalStyle.altTextColor)
		flexShrink(0)
		fontSize(0.72.cssRem)
		monoFont()
		opacity(0.8)
	}

	val newTag by style {
		border(1.px, LineStyle.Solid, GlobalStyle.logoLeftColor)
		borderRadius(0.25.cssRem)
		color(GlobalStyle.logoLeftColor)
		flexShrink(0)
		fontSize(0.65.cssRem)
		fontWeight(600)
		lineHeight(1.4)
		padding(0.px, 0.3.cssRem)
		textTransform(TextTransform.Uppercase)
	}

	val nestedList by style {
		listStyle(ListStyle.None)
		margin(0.px)
		paddingLeft(1.35.cssRem)
	}

	val pageRow by style {
		alignItems(AlignItems.Center)
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.textColor)
		display(DisplayStyle.Flex)
		fontSize(0.95.cssRem)
		gap(0.5.cssRem)
		minWidth(0.px)
		padding(0.15.cssRem, 0.3.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "color", "background-color")

		self + hover style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		}
	}

	val pageTitle by style {
		flex(1)
		minWidth(0.px)
		overflow(Overflow.Hidden)
		textOverflow(TextOverflow.Ellipsis)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val sectionLink by style {
		alignItems(AlignItems.Center)
		color(GlobalStyle.altTextColor)
		display(DisplayStyle.Flex)
		fontSize(0.95.cssRem)
		fontWeight(500)
		gap(0.55.cssRem)
		padding(0.3.cssRem, 0.45.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "color")

		child(self, type("svg")) style {
			flexShrink(0)
			fontSize(1.05.cssRem)
		}

		self + hover style {
			color(GlobalStyle.textColor)
		}
	}

	/** Declared after [sectionLink] so its color wins the cascade at equal specificity. */
	val currentSection by style {
		color(GlobalStyle.linkColor)
		fontWeight(600)

		self + hover style {
			color(GlobalStyle.linkColor)
		}
	}

	val sections by style {
		borderBottom(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.1.cssRem)
		listStyle(ListStyle.None)
		margin(0.75.cssRem, 0.px, 0.25.cssRem)
		padding(0.px, 0.px, 0.6.cssRem)
	}

	val selected by style {
		color(GlobalStyle.linkColor)
	}

	val title by style {
		flexShrink(0)
		fontSize(1.25.cssRem)
		fontWeight(FontWeight.Bold)
		marginY(0.6.cssRem)
	}
}
