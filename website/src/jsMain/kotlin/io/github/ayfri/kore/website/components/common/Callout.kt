package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.css.setVariable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.ariaHidden
import com.varabyte.kobweb.silk.components.icons.lucide.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.marginY
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

enum class CalloutType(
	val displayName: String,
	val color: CSSColorValue,
	val icon: @Composable () -> Unit,
) {
	CAUTION("Caution", Color("#ffc107"), { LucideOctagonAlert(Modifier.ariaHidden()) }),
	DANGER("Danger", Color("#d73a49"), { LucideCircleX(Modifier.ariaHidden()) }),
	IMPORTANT("Important", Color("#8b5cf6"), { LucideMessageSquareWarning(Modifier.ariaHidden()) }),
	INFO("Info", Color("#0078d4"), { LucideInfo(Modifier.ariaHidden()) }),
	NOTE("Note", Color("#0078d4"), { LucideNotepadText(Modifier.ariaHidden()) }),
	TIP("Tip", Color("#28a745"), { LucideLightbulb(Modifier.ariaHidden()) }),
	WARNING("Warning", Color("#ffc107"), { LucideTriangleAlert(Modifier.ariaHidden()) });

	val className get() = "callout-${name.lowercase()}"

	companion object {
		fun fromString(value: String): CalloutType = entries.find { it.name.equals(value, ignoreCase = true) } ?: NOTE
	}
}

private val CalloutColor by StyleVariable<CSSColorValue>()

object CalloutStyle : StyleSheet() {
	val callout by style {
		backgroundColor(CalloutColor.value().alpha(0.1))
		border(1.px, LineStyle.Solid, CalloutColor.value().alpha(0.35))
		borderRadius(GlobalStyle.roundingButton)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.75.cssRem)
		marginY(1.cssRem)
		padding(1.cssRem)
	}

	val calloutTitle by style {
		alignItems(AlignItems.Center)
		color(CalloutColor.value())
		display(DisplayStyle.Flex)
		fontSize(1.1.cssRem)
		fontWeight("bold")
		gap(0.5.cssRem)

		child(self, type("svg")) style {
			flexShrink(0)
		}
	}

	val calloutContent by style {
		lineHeight("1.6")
	}

	init {
		CalloutType.entries.forEach { type ->
			className(type.className) style {
				setVariable(CalloutColor, type.color)
			}
		}

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

	Div({ classes(CalloutStyle.callout, calloutType.className) }) {
		Div({ classes(CalloutStyle.calloutTitle) }) {
			calloutType.icon()
			Text(calloutType.displayName)
		}
		Div({ classes(CalloutStyle.calloutContent) }) {
			content()
		}
	}
}
