package io.github.ayfri.kore.features.dialogs.action

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent

data class DialogLabelledActionsContainer(var actions: List<DialogLabelledAction> = mutableListOf())

/** Adds a new [DialogLabelledAction]. */
fun DialogLabelledActionsContainer.action(label: ChatComponents, block: DialogLabelledAction.() -> Unit) = apply {
	actions += DialogLabelledAction(label = label).apply(block)
}

/** Adds a new [DialogLabelledAction]. */
fun DialogLabelledActionsContainer.action(label: String, block: DialogLabelledAction.() -> Unit) =
	action(textComponent(label), block)
