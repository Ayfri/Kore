package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.Dialogs
import io.github.ayfri.kore.features.dialogs.action.AfterAction
import io.github.ayfri.kore.features.dialogs.action.DialogAction
import io.github.ayfri.kore.features.dialogs.action.DialogActionContainer
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.features.dialogs.control.ControlContainer
import io.github.ayfri.kore.features.dialogs.control.DialogControl
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class MultiAction(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var afterAction: AfterAction? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	override var pause: Boolean? = null,
	var actions: List<DialogAction>? = null,
	var exitAction: DialogAction? = null,
	var columns: Int? = null,
	var inputs: List<DialogControl>,
) : DialogData()

fun Dialogs.multiAction(
	filename: String = "multi_action",
	title: ChatComponents,
	block: MultiAction.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, MultiAction(title, inputs = emptyList()).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

fun Dialogs.multiAction(
	filename: String = "multi_action",
	title: String,
	block: MultiAction.() -> Unit,
) = multiAction(filename, textComponent(title), block)

fun MultiAction.actions(block: DialogActionContainer.() -> Unit) {
	actions = DialogActionContainer().apply(block).actions
}

fun MultiAction.exitAction(label: ChatComponents, block: DialogAction.() -> Unit) {
	exitAction = DialogAction(label = label).apply(block)
}

fun MultiAction.exitAction(label: String, block: DialogAction.() -> Unit) = exitAction(textComponent(label), block)

fun MultiAction.inputs(block: ControlContainer.() -> Unit) {
	inputs = ControlContainer().apply(block).controls
}
