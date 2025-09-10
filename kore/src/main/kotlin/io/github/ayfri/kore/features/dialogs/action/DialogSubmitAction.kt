package io.github.ayfri.kore.features.dialogs.action

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.action.submit.CommandTemplate
import io.github.ayfri.kore.features.dialogs.action.submit.SubmitMethod
import io.github.ayfri.kore.features.dialogs.action.submit.SubmitMethodContainer
import kotlinx.serialization.Serializable

@Serializable
data class DialogSubmitAction(
	override var label: ChatComponents,
	override var tooltip: ChatComponents? = null,
	override var width: Int? = null,
	var id: String,
	var onSubmit: SubmitMethod = CommandTemplate(""),
) : DialogButton()

fun ActionsContainer<DialogSubmitAction>.submit(label: ChatComponents, id: String, block: DialogSubmitAction.() -> Unit) {
	val action = DialogSubmitAction(label, id=id).apply(block)
	actions += action
}

fun ActionsContainer<DialogSubmitAction>.submit(label: String, id: String, block: DialogSubmitAction.() -> Unit) =
	submit(textComponent(label), id, block)

fun DialogSubmitAction.onSubmit(block: SubmitMethodContainer.() -> Unit) {
	val container = SubmitMethodContainer().apply(block)
	onSubmit = container.method ?: CommandTemplate("")
}
