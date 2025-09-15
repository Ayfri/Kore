package io.github.ayfri.kore.features.dialogs.action

import io.github.ayfri.kore.arguments.actions.DialogAction
import io.github.ayfri.kore.arguments.actions.DialogActionContainer
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import kotlinx.serialization.Serializable

@Serializable
data class DialogLabelledAction(
	/** An action to perform when button is clicked. */
	var action: DialogAction? = null,
	/** The text component to display on the button. */
	var label: ChatComponents,
	/** Optional text component to display when button is highlighted or hovered over. */
	var tooltip: ChatComponents? = null,
	/** A width between 1 and 1024, defaults to 150. */
	var width: Int? = null,
)

/** Set the action to perform when the button is clicked. */
fun DialogLabelledAction.action(block: DialogActionContainer.() -> Unit) =
	apply { action = DialogActionContainer().apply(block).action }

/** Set the text component to display on the button. */
fun DialogLabelledAction.label(label: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	this.label = textComponent(label, color, block)
}

/** Set the text component to display when button is highlighted or hovered over. */
fun DialogLabelledAction.tooltip(tooltip: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	this.tooltip = textComponent(tooltip, color, block)
}
