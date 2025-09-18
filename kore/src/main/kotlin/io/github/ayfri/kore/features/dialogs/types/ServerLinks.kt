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

/**
 * A dialog screen with a scrollable list of server links, arranged in columns.
 * If [exitAction] is present, a button for it will appear in the footer, otherwise the footer is not present.
 * [exitAction] is also used for the Escape action.
 */
@Serializable
data class ServerLinks(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var afterAction: AfterAction? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	override var inputs: List<DialogControl> = emptyList(),
	override var pause: Boolean? = null,
	/** Value between 1 and 1024 — Width of the button. Defaults to 150. */
	var buttonWidth: Int? = null,
	/** Positive integer describing the number of columns. Defaults to 2. */
	var columns: Int? = null,
	/** Action performed when the exit button is clicked. Defaults to [AfterAction.CLOSE]. */
	var exitAction: DialogLabelledAction? = null,
) : DialogData()

/** Create and register a [ServerLinks] dialog to the specified [DataPack].
 *
 * Produces `data/<namespace>/dialog/<fileName>.json`.
 *
 * Minecraft Wiki: [https://minecraft.wiki/w/Dialog#server_links](https://minecraft.wiki/w/Dialog#server_links)
 */
fun DialogContainer.serverLinks(
	filename: String = "server_links",
	title: ChatComponents,
	block: ServerLinks.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, ServerLinks(title).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

/** Create and register a [ServerLinks] dialog to the specified [DataPack].
 *
 * Produces `data/<namespace>/dialog/<fileName>.json`.
 *
 * Minecraft Wiki: [https://minecraft.wiki/w/Dialog#server_links](https://minecraft.wiki/w/Dialog#server_links)
 */
fun DialogContainer.serverLinks(
	filename: String = "server_links",
	title: String,
	block: ServerLinks.() -> Unit,
) = serverLinks(filename, textComponent(title), block)

/** Set the [exitAction] of the dialog. */
fun ServerLinks.exitAction(label: ChatComponents, block: DialogLabelledAction.() -> Unit) {
	exitAction = DialogLabelledAction(label = label).apply(block)
}

/** Set the [exitAction] of the dialog. */
fun ServerLinks.exitAction(label: String, block: DialogLabelledAction.() -> Unit) = exitAction(textComponent(label), block)
