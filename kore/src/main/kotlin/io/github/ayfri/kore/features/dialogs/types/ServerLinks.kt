package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.click.ClickEvent
import io.github.ayfri.kore.arguments.chatcomponents.click.ClickEventContainer
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.Dialogs
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class ServerLinks(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	var onCancel: ClickEvent? = null,
	var columns: Int? = null,
	var buttonWidth: Int? = null,
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

fun ServerLinks.onCancel(block: ClickEventContainer.() -> Unit) {
	onCancel = ClickEventContainer().apply(block).event
}
