package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.Dialogs
import io.github.ayfri.kore.features.dialogs.action.DialogSubmitAction
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.features.dialogs.control.ControlContainer
import io.github.ayfri.kore.features.dialogs.control.DialogControl
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class SimpleInputForm(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	var inputs: List<DialogControl>,
	var action: DialogSubmitAction? = null,
) : DialogData()

fun Dialogs.simpleInputForm(
	filename: String = "simple_input_form",
	title: ChatComponents,
	block: SimpleInputForm.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, SimpleInputForm(title, inputs = emptyList()).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

fun Dialogs.simpleInputForm(
	filename: String = "simple_input_form",
	title: String,
	block: SimpleInputForm.() -> Unit,
) = simpleInputForm(filename, textComponent(title), block)

fun SimpleInputForm.action(label: ChatComponents, id: String, block: DialogSubmitAction.() -> Unit) {
	action = DialogSubmitAction(label, id = id).apply(block)
}

fun SimpleInputForm.action(label: String, id: String, block: DialogSubmitAction.() -> Unit) =
	action(textComponent(label), id, block)

fun SimpleInputForm.inputs(block: ControlContainer.() -> Unit) {
	inputs = ControlContainer().apply(block).controls
}

fun SimpleInputForm.onSubmit(label: ChatComponents, id: String, block: DialogSubmitAction.() -> Unit) {
	action = DialogSubmitAction(label, id = id).apply(block)
}

fun SimpleInputForm.onSubmit(label: String, id: String, block: DialogSubmitAction.() -> Unit) =
	onSubmit(textComponent(label), id, block)
