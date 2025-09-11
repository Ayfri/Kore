package io.github.ayfri.kore.features.dialogs.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.features.dialogs.action.AfterAction
import io.github.ayfri.kore.features.dialogs.body.BodyContainer
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DialogData.Companion.DialogDataSerializer::class)
sealed class DialogData {
	abstract var title: ChatComponents
	abstract var externalTitle: ChatComponents?
	abstract var afterAction: AfterAction?
	abstract var body: InlinableList<DialogBody>?
	abstract var canCloseWithEscape: Boolean?
	abstract var pause: Boolean?

	companion object {
		data object DialogDataSerializer : NamespacedPolymorphicSerializer<DialogData>(DialogData::class)
	}
}

fun DialogData.bodies(block: BodyContainer.() -> Unit) = apply {
	this.body = BodyContainer().apply(block).bodies
}

fun DialogData.externalTitle(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	externalTitle = textComponent(text, color, block)
}

fun DialogData.title(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	title = textComponent(text, color, block)
}
