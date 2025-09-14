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
	var action: DialogAction? = null,
	var label: ChatComponents,
	var tooltip: ChatComponents? = null,
	var width: Int? = null,
)

fun DialogLabelledAction.action(block: DialogActionContainer.() -> Unit) =
	apply { action = DialogActionContainer().apply(block).action }

fun DialogLabelledAction.label(label: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	this.label = textComponent(label, color, block)
}

fun DialogLabelledAction.tooltip(tooltip: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	this.tooltip = textComponent(tooltip, color, block)
}


data class DialogLabelledActionsContainer(var actions: List<DialogLabelledAction> = mutableListOf())

fun DialogLabelledActionsContainer.action(label: ChatComponents, block: DialogLabelledAction.() -> Unit) = apply {
	actions += DialogLabelledAction(label = label).apply(block)
}

fun DialogLabelledActionsContainer.action(label: String, block: DialogLabelledAction.() -> Unit) =
	action(textComponent(label), block)
