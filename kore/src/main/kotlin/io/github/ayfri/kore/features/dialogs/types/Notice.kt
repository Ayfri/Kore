package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.Dialogs
import io.github.ayfri.kore.features.dialogs.action.AfterAction
import io.github.ayfri.kore.features.dialogs.action.DialogAction
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Notice(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var afterAction: AfterAction? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	override var pause: Boolean? = null,
	var action: DialogAction? = null,
) : DialogData()

fun Dialogs.notice(
	filename: String = "notice",
	title: ChatComponents,
	block: Notice.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, Notice(title).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

fun Dialogs.notice(
	filename: String = "notice",
	title: String,
	block: Notice.() -> Unit,
) = notice(filename, textComponent(title), block)

fun Notice.action(label: ChatComponents, block: DialogAction.() -> Unit) {
	action = DialogAction(label = label).apply(block)
}
fun Notice.action(label: String, block: DialogAction.() -> Unit) = action(textComponent(label), block)
