package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.DialogContainer
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
	/** The action performed when the [yes] button is clicked. */
	var yes: DialogLabelledAction = DialogLabelledAction(label = textComponent()),
	/** The action performed when the [no] button is clicked. Defaults to [AfterAction.CLOSE]. */
	var no: DialogLabelledAction = DialogLabelledAction(label = textComponent()),
) : DialogData()

/** Create and register a [Confirmation] dialog to the specified [DataPack].
 *
 * Produces `data/<namespace>/dialog/<fileName>.json`.
 *
 * Minecraft Wiki: [https://minecraft.wiki/w/Dialog#confirmation](https://minecraft.wiki/w/Dialog#confirmation)
 */
fun DialogContainer.confirmation(
	filename: String = "confirmation",
	title: ChatComponents,
	block: Confirmation.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, Confirmation(title).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

/** Create and register a [Confirmation] dialog to the specified [DataPack].
 *
 * Produces `data/<namespace>/dialog/<fileName>.json`.
 *
 * Minecraft Wiki: [https://minecraft.wiki/w/Dialog#confirmation](https://minecraft.wiki/w/Dialog#confirmation)
 */
fun DialogContainer.confirmation(
	filename: String = "confirmation",
	title: String,
	block: Confirmation.() -> Unit,
) = confirmation(filename, textComponent(title), block)

/** Set the [yes] action of the dialog. */
fun Confirmation.yes(label: ChatComponents, block: DialogLabelledAction.() -> Unit) {
	yes = DialogLabelledAction(label = label).apply(block)
}

/** Set the [yes] action of the dialog. */
fun Confirmation.yes(label: String, block: DialogLabelledAction.() -> Unit) = yes(textComponent(label), block)

/** Set the [no] action of the dialog. */
fun Confirmation.no(label: ChatComponents, block: DialogLabelledAction.() -> Unit) {
	no = DialogLabelledAction(label = label).apply(block)
}

/** Set the [no] action of the dialog. */
fun Confirmation.no(label: String, block: DialogLabelledAction.() -> Unit) = no(textComponent(label), block)
