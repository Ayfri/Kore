package io.github.ayfri.kore.features.dialogs.action

import io.github.ayfri.kore.arguments.actions.Action
import io.github.ayfri.kore.arguments.actions.ActionContainer
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import kotlinx.serialization.Serializable

@Serializable
data class DialogAction(
	var action: Action? = null,
	var label: ChatComponents,
	var tooltip: ChatComponents? = null,
	var width: Int? = null
)

fun DialogAction.action(block: ActionContainer.() -> Unit) = apply { action = ActionContainer().apply(block).action }

fun DialogAction.label(label: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	this.label = textComponent(label, color, block)
}

fun DialogAction.tooltip(tooltip: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	this.tooltip = textComponent(tooltip, color, block)
}


data class DialogActionContainer(var actions: List<DialogAction> = mutableListOf())

fun DialogActionContainer.action(label: ChatComponents, block: DialogAction.() -> Unit) = apply {
	actions += DialogAction(label = label).apply(block)
}

fun DialogActionContainer.action(label: String, block: DialogAction.() -> Unit) = action(textComponent(label), block)
