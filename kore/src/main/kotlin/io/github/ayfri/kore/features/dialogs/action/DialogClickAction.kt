package io.github.ayfri.kore.features.dialogs.action

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.actions.Action
import io.github.ayfri.kore.arguments.actions.ActionContainer
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import kotlinx.serialization.Serializable

@Serializable
data class DialogClickAction(
	override var label: ChatComponents,
	override var tooltip: ChatComponents? = null,
	override var width: Int? = null,
	var onClick: Action? = null,
) : DialogButton()

fun ActionsContainer<DialogClickAction>.click(label: ChatComponents, block: DialogClickAction.() -> Unit) {
	val action = DialogClickAction(label).apply(block)
	actions += action
}

fun ActionsContainer<DialogClickAction>.click(label: String, block: DialogClickAction.() -> Unit) =
	click(textComponent(label), block)


fun DialogClickAction.label(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	label = textComponent(text, color, block)
}

fun DialogClickAction.tooltip(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	tooltip = textComponent(text, color, block)
}

fun DialogClickAction.onClick(block: ActionContainer.() -> Unit) {
	onClick = ActionContainer().apply(block).action
}
