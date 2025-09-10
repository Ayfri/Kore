package io.github.ayfri.kore.features.dialogs.action

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DialogButton.Companion.DialogButtonSerializer::class)
sealed class DialogButton {
	abstract var label: ChatComponents
	abstract var tooltip: ChatComponents?
	abstract var width: Int?

	companion object {
		data object DialogButtonSerializer : NamespacedPolymorphicSerializer<DialogButton>(DialogButton::class)
	}
}

fun DialogButton.label(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	this.label = textComponent(text, color, block)
}

fun DialogButton.tooltip(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	tooltip = textComponent(text, color, block)
}
