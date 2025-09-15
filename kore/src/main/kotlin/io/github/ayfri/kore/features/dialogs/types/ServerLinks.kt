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

@Serializable
data class ServerLinks(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var afterAction: AfterAction? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	override var inputs: List<DialogControl> = emptyList(),
	override var pause: Boolean? = null,
	var buttonWidth: Int? = null,
	var columns: Int? = null,
	var exitAction: DialogLabelledAction? = null,
) : DialogData()

fun Dialogs.serverLinks(
	filename: String = "server_links",
	title: ChatComponents,
	block: ServerLinks.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, ServerLinks(title).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

fun Dialogs.serverLinks(
	filename: String = "server_links",
	title: String,
	block: ServerLinks.() -> Unit,
) = serverLinks(filename, textComponent(title), block)

fun ServerLinks.exitAction(label: ChatComponents, block: DialogLabelledAction.() -> Unit) {
	exitAction = DialogLabelledAction(label = label).apply(block)
}

fun ServerLinks.exitAction(label: String, block: DialogLabelledAction.() -> Unit) = exitAction(textComponent(label), block)
