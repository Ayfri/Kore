package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.DialogContainer
import io.github.ayfri.kore.features.dialogs.action.AfterAction
import io.github.ayfri.kore.features.dialogs.action.DialogLabelledAction
import io.github.ayfri.kore.features.dialogs.action.DialogLabelledActionsContainer
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.features.dialogs.control.DialogControl
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/** A dialog screen with a scrollable list of action buttons arranged in columns.
 * If [exitAction] is present, a button for it will appear in the footer, otherwise the footer is not present.
 * [exitAction] is also used for the Escape action.
 */
@Serializable
data class MultiAction(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var afterAction: AfterAction? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	override var inputs: List<DialogControl> = emptyList(),
	override var pause: Boolean? = null,
	/** Non-empty list of click actions. */
	var actions: List<DialogLabelledAction>? = null,
	/** Action performed when the exit button is clicked. Defaults to [AfterAction.CLOSE]. */
	var exitAction: DialogLabelledAction? = null,
	/** Positive integer describing the number of columns. Defaults to 2. */
	var columns: Int? = null,
) : DialogData()

/** Create and register a [MultiAction] dialog to the specified [DataPack].
 *
 * Produces `data/<namespace>/dialog/<fileName>.json`.
 *
 * Minecraft Wiki: [https://minecraft.wiki/w/Dialog#multi_action](https://minecraft.wiki/w/Dialog#multi_action)
 */
fun DialogContainer.multiAction(
	filename: String = "multi_action",
	title: ChatComponents,
	block: MultiAction.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, MultiAction(title, inputs = emptyList()).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

/** Create and register a [MultiAction] dialog to the specified [DataPack].
 *
 * Produces `data/<namespace>/dialog/<fileName>.json`.
 *
 * Minecraft Wiki: [https://minecraft.wiki/w/Dialog#multi_action](https://minecraft.wiki/w/Dialog#multi_action)
 */
fun DialogContainer.multiAction(
	filename: String = "multi_action",
	title: String,
	block: MultiAction.() -> Unit,
) = multiAction(filename, textComponent(title), block)

/** Set the [actions] of the dialog. */
fun MultiAction.actions(block: DialogLabelledActionsContainer.() -> Unit) {
	actions = DialogLabelledActionsContainer().apply(block).actions
}

/** Set the [exitAction] of the dialog. */
fun MultiAction.exitAction(label: ChatComponents, block: DialogLabelledAction.() -> Unit) {
	exitAction = DialogLabelledAction(label = label).apply(block)
}

/** Set the [exitAction] of the dialog. */
fun MultiAction.exitAction(label: String, block: DialogLabelledAction.() -> Unit) = exitAction(textComponent(label), block)
