package io.github.ayfri.kore.features.dialogs.types

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

/** A dialog screen with a single action button in footer, specified by [action].
 * By default, the exit action (which returns the player back to gameplay) is the same [action] button.
 */
@Serializable
data class Notice(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var afterAction: AfterAction? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	override var inputs: List<DialogControl> = emptyList(),
	override var pause: Boolean? = null,
	/** The action performed when the [action] button is clicked. */
	var action: DialogLabelledAction? = null,
) : DialogData()

/** Create and register a [Notice] dialog to the specified [DataPack].
 *
 * Produces `data/<namespace>/dialog/<fileName>.json`.
 *
 * Minecraft Wiki: [https://minecraft.wiki/w/Dialog#notice](https://minecraft.wiki/w/Dialog#notice)
 */
fun DialogContainer.notice(
	filename: String = "notice",
	title: ChatComponents,
	block: Notice.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, Notice(title).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

/** Create and register a [Notice] dialog to the specified [DataPack].
 *
 * Produces `data/<namespace>/dialog/<fileName>.json`.
 *
 * Minecraft Wiki: [https://minecraft.wiki/w/Dialog#notice](https://minecraft.wiki/w/Dialog#notice)
 */
fun DialogContainer.notice(
	filename: String = "notice",
	title: String,
	block: Notice.() -> Unit,
) = notice(filename, textComponent(title), block)

/** Set the [action] of the dialog. */
fun Notice.action(label: ChatComponents, block: DialogLabelledAction.() -> Unit) {
	action = DialogLabelledAction(label = label).apply(block)
}

/** Set the [action] of the dialog. */
fun Notice.action(label: String, block: DialogLabelledAction.() -> Unit) = action(textComponent(label), block)
