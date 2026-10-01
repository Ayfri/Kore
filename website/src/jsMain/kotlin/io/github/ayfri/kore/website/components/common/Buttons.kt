@file:OptIn(ExperimentalComposeWebApi::class)

package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.externalTarget
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text

enum class ButtonVariant {
	CONTAINED,
	OUTLINE,
	GHOST
}

enum class ButtonColor {
	PRIMARY,
	SECONDARY,
	BRAND
}

@Composable
fun LinkButton(
	name: String,
	link: String,
	variant: ButtonVariant = ButtonVariant.CONTAINED,
	color: ButtonColor = ButtonColor.SECONDARY,
	icon: @Composable () -> Unit = {},
	vararg classes: String,
) {
	A(link, {
		classes(ButtonStyle.button, *classes)
		classes(getVariantClass(variant, color))
		externalTarget(link)
	}) {
		icon()
		Text(name)
	}
}

@Composable
fun Button(
	name: String,
	onClick: () -> Unit = {},
	variant: ButtonVariant = ButtonVariant.CONTAINED,
	color: ButtonColor = ButtonColor.SECONDARY,
	icon: @Composable () -> Unit = {},
	vararg classes: String,
) {
	Button({
		classes(ButtonStyle.button, *classes)
		classes(getVariantClass(variant, color))
		onClick { onClick() }
	}) {
		icon()
		Text(name)
	}
}

private fun getVariantClass(variant: ButtonVariant, color: ButtonColor) = when (variant) {
	ButtonVariant.CONTAINED -> when (color) {
		ButtonColor.PRIMARY -> ButtonStyle.primaryContained
		ButtonColor.SECONDARY -> ButtonStyle.secondaryContained
		ButtonColor.BRAND -> ButtonStyle.brandContained
	}

	ButtonVariant.OUTLINE -> when (color) {
		ButtonColor.PRIMARY -> ButtonStyle.primaryOutline
		ButtonColor.SECONDARY -> ButtonStyle.secondaryOutline
		ButtonColor.BRAND -> ButtonStyle.brandOutline
	}

	ButtonVariant.GHOST -> when (color) {
		ButtonColor.PRIMARY -> ButtonStyle.primaryGhost
		ButtonColor.SECONDARY -> ButtonStyle.secondaryGhost
		ButtonColor.BRAND -> ButtonStyle.brandGhost
	}
}

/** Each variant repeats its text color on hover, else the global `a:hover` rule outranks the class and tints link buttons teal. */
object ButtonStyle : StyleSheet() {
	@OptIn(ExperimentalComposeWebApi::class)
	val button by style {
		alignItems(AlignItems.Center)
		border(0.px)
		borderRadius(GlobalStyle.roundingButton)
		cursor(Cursor.Pointer)
		display(DisplayStyle.LegacyInlineFlex)
		fontSize(1.1.cssRem)
		fontWeight(FontWeight.Bold)
		gap(0.5.cssRem)
		justifyContent(JustifyContent.Center)
		padding(0.6.cssRem, 1.2.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "background-color", "color", "border-color", "transform")

		self + hover style {
			transform { translateY((-1).px) }
		}

		self + active style {
			transform { translateY(0.px) }
		}
	}

	// Contained
	val primaryContained by style {
		backgroundColor(GlobalStyle.buttonBackgroundColor)
		color(GlobalStyle.textColor)

		self + hover style {
			backgroundColor(GlobalStyle.buttonBackgroundColorHover)
			color(GlobalStyle.textColor)
		}
	}

	val secondaryContained by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		color(GlobalStyle.textColor)

		self + hover style {
			backgroundColor(GlobalStyle.secondaryBackgroundColor)
			color(GlobalStyle.textColor)
		}
	}

	val brandContained by style {
		backgroundColor(GlobalStyle.logoRightColor)
		color(GlobalStyle.textColor)

		self + hover style {
			backgroundColor(GlobalStyle.logoRightColor.alpha(0.8))
			color(GlobalStyle.textColor)
		}
	}

	// Outline
	val primaryOutline by style {
		backgroundColor(Color.transparent)
		border(1.px, LineStyle.Solid, GlobalStyle.buttonBackgroundColor)
		color(GlobalStyle.buttonBackgroundColor)

		self + hover style {
			backgroundColor(GlobalStyle.buttonBackgroundColor.alpha(0.12))
			color(GlobalStyle.buttonBackgroundColor)
		}
	}

	val secondaryOutline by style {
		backgroundColor(Color.transparent)
		border(1.px, LineStyle.Solid, GlobalStyle.borderColor.alpha(0.5))
		color(GlobalStyle.textColor)

		self + hover style {
			backgroundColor(GlobalStyle.textColor.alpha(0.05))
			color(GlobalStyle.textColor)
		}
	}

	val brandOutline by style {
		backgroundColor(Color.transparent)
		border(1.px, LineStyle.Solid, GlobalStyle.logoRightColor)
		color(GlobalStyle.logoRightColor)

		self + hover style {
			backgroundColor(GlobalStyle.logoRightColor.alpha(0.12))
			color(GlobalStyle.logoRightColor)
		}
	}

	// Ghost
	val primaryGhost by style {
		backgroundColor(Color.transparent)
		color(GlobalStyle.buttonBackgroundColor)

		self + hover style {
			backgroundColor(GlobalStyle.buttonBackgroundColor.alpha(0.12))
			color(GlobalStyle.buttonBackgroundColor)
		}
	}

	val secondaryGhost by style {
		backgroundColor(Color.transparent)
		color(GlobalStyle.altTextColor)

		self + hover style {
			backgroundColor(GlobalStyle.textColor.alpha(0.05))
			color(GlobalStyle.textColor)
		}
	}

	val brandGhost by style {
		backgroundColor(Color.transparent)
		color(GlobalStyle.logoRightColor)

		self + hover style {
			backgroundColor(GlobalStyle.logoRightColor.alpha(0.12))
			color(GlobalStyle.logoRightColor)
		}
	}
}
