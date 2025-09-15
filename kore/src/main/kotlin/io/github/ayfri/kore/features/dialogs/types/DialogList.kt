package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.Dialogs
import io.github.ayfri.kore.features.dialogs.action.AfterAction
import io.github.ayfri.kore.features.dialogs.action.DialogLabelledAction
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.features.dialogs.control.DialogControl
import io.github.ayfri.kore.generated.arguments.DialogOrTagArgument
import io.github.ayfri.kore.generated.arguments.tagged.DialogTagArgument
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class DialogList(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var afterAction: AfterAction? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var inputs: List<DialogControl> = emptyList(),
	override var canCloseWithEscape: Boolean? = null,
	override var pause: Boolean? = null,
	var dialogs: InlinableList<DialogOrTagArgument>,
	var exitAction: DialogLabelledAction? = null,
	var columns: Int? = null,
	var buttonWidth: Int? = null,
) : DialogData()

fun Dialogs.dialogList(
	filename: String = "list",
	title: ChatComponents,
	block: DialogList.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, DialogList(title, dialogs = emptyList()).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

fun Dialogs.dialogList(
	filename: String = "list",
	title: String,
	block: DialogList.() -> Unit,
) = dialogList(filename, textComponent(title), block)

fun DialogList.dialogs(vararg dialogs: DialogArgument) {
	this.dialogs = dialogs.toList()
}
fun DialogList.dialogs(dialogs: DialogTagArgument) {
	this.dialogs = listOf(dialogs)
}

fun DialogList.exitAction(label: ChatComponents, block: DialogLabelledAction.() -> Unit) {
	exitAction = DialogLabelledAction(label = label).apply(block)
}

fun DialogList.exitAction(label: String, block: DialogLabelledAction.() -> Unit) = exitAction(textComponent(label), block)
