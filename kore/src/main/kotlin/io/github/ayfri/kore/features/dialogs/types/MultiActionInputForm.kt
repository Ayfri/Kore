package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.Dialogs
import io.github.ayfri.kore.features.dialogs.action.ActionsContainer
import io.github.ayfri.kore.features.dialogs.action.DialogSubmitAction
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.features.dialogs.control.ControlContainer
import io.github.ayfri.kore.features.dialogs.control.DialogControl
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class MultiActionInputForm(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	var inputs: List<DialogControl>,
	var actions: List<DialogSubmitAction>? = null,
	var columns: Int? = null,
) : DialogData()

fun Dialogs.multiActionInputForm(
	filename: String = "multi_action_input_form",
	title: ChatComponents,
	block: MultiActionInputForm.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, MultiActionInputForm(title, inputs = emptyList()).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

fun Dialogs.multiActionInputForm(
	filename: String = "multi_action_input_form",
	title: String,
	block: MultiActionInputForm.() -> Unit,
) = multiActionInputForm(filename, textComponent(title), block)

fun MultiActionInputForm.actions(block: ActionsContainer<DialogSubmitAction>.() -> Unit) {
	actions = ActionsContainer<DialogSubmitAction>().apply(block).actions
}

fun MultiActionInputForm.inputs(block: ControlContainer.() -> Unit) {
	inputs = ControlContainer().apply(block).controls
}
