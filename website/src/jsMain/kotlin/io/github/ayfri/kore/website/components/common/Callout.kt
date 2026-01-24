package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.borderLeft
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.ariaHidden
import com.varabyte.kobweb.silk.components.icons.mdi.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.marginY
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

enum class CalloutType(
	val displayName: String,
	val color: CSSColorValue,
	val icon: @Composable () -> Unit,
) {
	CAUTION("Caution", Color("#ffc107"), { MdiWarning(Modifier.ariaHidden()) }),
	DANGER("Danger", Color("#d73a49"), { MdiError(Modifier.ariaHidden()) }),
	IMPORTANT("Important", Color("#8b5cf6"), { MdiPriorityHigh(Modifier.ariaHidden()) }),
	INFO("Info", Color("#0078d4"), { MdiInfo(Modifier.ariaHidden()) }),
	NOTE("Note", Color("#0078d4"), { MdiInfo(Modifier.ariaHidden()) }),
	TIP("Tip", Color("#28a745"), { MdiLightbulb(Modifier.ariaHidden()) }),
	WARNING("Warning", Color("#ffc107"), { MdiWarning(Modifier.ariaHidden()) });

	companion object {
		fun fromString(value: String): CalloutType = entries.find { it.name.equals(value, ignoreCase = true) } ?: NOTE
	}
}

object CalloutStyle : StyleSheet() {
	val callout by style {
		borderRadius(GlobalStyle.roundingButton)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.75.cssRem)
		marginY(1.cssRem)
		padding(1.cssRem)
	}

	val calloutTitle by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		fontSize(1.1.cssRem)
		fontWeight("bold")
		gap(0.5.cssRem)
	}

	val calloutContent by style {
		lineHeight("1.6")
	}

	init {
		".$calloutContent p" style {
			margin(0.px)
		}

		".$calloutContent b" style {
			display(DisplayStyle.InlineBlock)
			margin(0.px, 0.2.cssRem)
		}
	}
}

@Composable
fun Callout(type: String, content: @Composable () -> Unit) {
	val calloutType = CalloutType.fromString(type)

	Div({
		classes(CalloutStyle.callout)
		style {
			property("background-color", "color-mix(in srgb, ${calloutType.color}, transparent 90%)")
			borderLeft(4.px, LineStyle.Solid, calloutType.color)
		}
	}) {
		Div({
			classes(CalloutStyle.calloutTitle)
			style {
				color(calloutType.color)
			}
		}) {
			calloutType.icon()
			Text(calloutType.displayName)
		}
		Div({ classes(CalloutStyle.calloutContent) }) {
			content()
		}
	}
}
