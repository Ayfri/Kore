package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.Dialogs
import io.github.ayfri.kore.features.dialogs.action.DialogAction
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Confirmation(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	var yes: DialogAction = DialogAction(label = textComponent("")),
	var no: DialogAction = DialogAction(label = textComponent(""))
) : DialogData()

fun Dialogs.confirmation(
	filename: String = "confirmation",
	title: ChatComponents,
	block: Confirmation.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, Confirmation(title).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

fun Dialogs.confirmation(
	filename: String = "confirmation",
	title: String,
	block: Confirmation.() -> Unit,
) = confirmation(filename, textComponent(title), block)

fun Confirmation.yes(label: ChatComponents, block: DialogAction.() -> Unit) {
	yes = DialogAction(label = label).apply(block)
}

fun Confirmation.yes(label: String, block: DialogAction.() -> Unit) = yes(textComponent(label), block)

fun Confirmation.no(label: ChatComponents, block: DialogAction.() -> Unit) {
	no = DialogAction(label = label).apply(block)
}

fun Confirmation.no(label: String, block: DialogAction.() -> Unit) = no(textComponent(label), block)
