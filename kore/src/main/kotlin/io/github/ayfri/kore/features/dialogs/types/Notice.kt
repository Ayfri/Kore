package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.Dialogs
import io.github.ayfri.kore.features.dialogs.action.DialogClickAction
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Notice(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	var action: DialogClickAction? = null,
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

fun Notice.action(label: ChatComponents, block: DialogClickAction.() -> Unit) {
	action = DialogClickAction(label).apply(block)
}
fun Notice.action(label: String, block: DialogClickAction.() -> Unit) = action(textComponent(label), block)
