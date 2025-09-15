package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.Dialogs
import io.github.ayfri.kore.features.dialogs.action.AfterAction
import io.github.ayfri.kore.features.dialogs.action.DialogLabelledAction
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.features.dialogs.control.DialogControl
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/** A dialog screen with two action buttons in footer, specified by [yes] and [no]. By default, the exit action is [no] button.*/
@Serializable
data class Confirmation(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var afterAction: AfterAction? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	override var inputs: List<DialogControl> = emptyList(),
	override var pause: Boolean? = null,
	var yes: DialogLabelledAction = DialogLabelledAction(label = textComponent()),
	var no: DialogLabelledAction = DialogLabelledAction(label = textComponent()),
) : DialogData()

/** Creates a confirmation dialog, . */
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

fun Confirmation.yes(label: ChatComponents, block: DialogLabelledAction.() -> Unit) {
	yes = DialogLabelledAction(label = label).apply(block)
}

fun Confirmation.yes(label: String, block: DialogLabelledAction.() -> Unit) = yes(textComponent(label), block)

fun Confirmation.no(label: ChatComponents, block: DialogLabelledAction.() -> Unit) {
	no = DialogLabelledAction(label = label).apply(block)
}

fun Confirmation.no(label: String, block: DialogLabelledAction.() -> Unit) = no(textComponent(label), block)
