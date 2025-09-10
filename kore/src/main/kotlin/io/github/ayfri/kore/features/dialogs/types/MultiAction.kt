package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.click.ClickEvent
import io.github.ayfri.kore.arguments.chatcomponents.click.ClickEventContainer
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.Dialogs
import io.github.ayfri.kore.features.dialogs.action.ActionsContainer
import io.github.ayfri.kore.features.dialogs.action.DialogClickAction
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class MultiAction(
	override var title: ChatComponents,
	override var externalTitle: ChatComponents? = null,
	override var body: InlinableList<DialogBody>? = null,
	override var canCloseWithEscape: Boolean? = null,
	var actions: List<DialogClickAction>? = null,
	var onCancel: ClickEvent? = null,
	var columns: Int? = null,
) : DialogData()

fun Dialogs.multiAction(
	filename: String = "multiAction",
	title: ChatComponents,
	block: MultiAction.() -> Unit,
): DialogArgument {
	val dialog = Dialog(filename, MultiAction(title).apply(block))
	dp.dialogs += dialog
	return DialogArgument(filename, dialog.namespace ?: dp.name)
}

fun Dialogs.multiAction(
	filename: String = "multiAction",
	title: String,
	block: MultiAction.() -> Unit,
) = multiAction(filename, textComponent(title), block)

fun MultiAction.actions(block: ActionsContainer<DialogClickAction>.() -> Unit) {
	actions = ActionsContainer<DialogClickAction>().apply(block).actions
}

fun MultiAction.onCancel(block: ClickEventContainer.() -> Unit) {
	onCancel = ClickEventContainer().apply(block).event
}
