package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.Dialogs
import io.github.ayfri.kore.features.dialogs.action.AfterAction
import io.github.ayfri.kore.features.dialogs.action.DialogLabelledAction
import io.github.ayfri.kore.features.dialogs.action.DialogLabelledActionsContainer
import io.github.ayfri.kore.features.dialogs.body.DialogBody
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
	override var inputs: List<DialogControl> = emptyList(),
	override var pause: Boolean? = null,
	var actions: List<DialogLabelledAction>? = null,
	var exitAction: DialogLabelledAction? = null,
	var columns: Int? = null,
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

fun MultiAction.actions(block: DialogLabelledActionsContainer.() -> Unit) {
	actions = DialogLabelledActionsContainer().apply(block).actions
}

fun MultiAction.exitAction(label: ChatComponents, block: DialogLabelledAction.() -> Unit) {
	exitAction = DialogLabelledAction(label = label).apply(block)
}

fun MultiAction.exitAction(label: String, block: DialogLabelledAction.() -> Unit) = exitAction(textComponent(label), block)
