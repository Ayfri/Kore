package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.DialogContainer
import io.github.ayfri.kore.features.dialogs.action.AfterAction
import io.github.ayfri.kore.features.dialogs.action.DialogLabelledAction
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.features.dialogs.control.DialogControl
import io.github.ayfri.kore.generated.arguments.DialogOrTagArgument
import io.github.ayfri.kore.generated.arguments.tagged.DialogTagArgument
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/** A dialog screen with scrollable list of buttons leading directly to other dialogs, arranged in columns.
 * Titles of those buttons will be taken from [externalTitle] fields of targeted dialogs.
 * If [exitAction] is present, a button for it will appear in the footer, otherwise the footer is not present.
 * [exitAction] is also used for the `Escape` action.
 */
@Serializable
data class DialogList(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var afterAction: AfterAction? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var inputs: List<DialogControl> = emptyList(),
	override var canCloseWithEscape: Boolean? = null,
	override var pause: Boolean? = null,
	/** The dialogs or tag dialog to display. */
	var dialogs: InlinableList<DialogOrTagArgument>,
	/** The action performed when the exit button is clicked. Defaults to [AfterAction.CLOSE]. */
	var exitAction: DialogLabelledAction? = null,
	/** Positive integer describing the number of columns. Defaults to 2. */
	var columns: Int? = null,
	/** Value between 0 and 1024 describing the width of buttons. Defaults to 150. */
	var buttonWidth: Int? = null,
) : DialogData()

/** Create and register a [DialogList] dialog to the specified [DataPack].
 *
 * Produces `data/<namespace>/dialog/<fileName>.json`.
 *
 * Minecraft Wiki: [https://minecraft.wiki/w/Dialog#dialog_list](https://minecraft.wiki/w/Dialog#dialog_list)
 */
fun DialogContainer.dialogList(
	filename: String = "list",
	title: ChatComponents,
	block: DialogList.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, DialogList(title, dialogs = emptyList()).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

/** Create and register a [DialogList] dialog to the specified [DataPack].
 *
 * Produces `data/<namespace>/dialog/<fileName>.json`.
 *
 * Minecraft Wiki: [https://minecraft.wiki/w/Dialog#list](https://minecraft.wiki/w/Dialog#list)
 */
fun DialogContainer.dialogList(
	filename: String = "list",
	title: String,
	block: DialogList.() -> Unit,
) = dialogList(filename, textComponent(title), block)

/** Set the [dialogs] of the dialog. */
fun DialogList.dialogs(vararg dialogs: DialogArgument) {
	this.dialogs = dialogs.toList()
}

/** Set the [dialogs] tag of the dialog. */
fun DialogList.dialogs(dialogs: DialogTagArgument) {
	this.dialogs = listOf(dialogs)
}

/** Set the [exitAction] of the dialog. */
fun DialogList.exitAction(label: ChatComponents, block: DialogLabelledAction.() -> Unit) {
	exitAction = DialogLabelledAction(label = label).apply(block)
}

/** Set the [exitAction] of the dialog. */
fun DialogList.exitAction(label: String, block: DialogLabelledAction.() -> Unit) = exitAction(textComponent(label), block)
