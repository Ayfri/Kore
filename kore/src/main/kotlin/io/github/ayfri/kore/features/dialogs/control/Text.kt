package io.github.ayfri.kore.features.dialogs.control

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import kotlinx.serialization.Serializable

@Serializable
data class Text(
	override var key: String,
	var width: Int? = null,
	var label: ChatComponents,
	var labelVisible: Boolean? = null,
	var initial: String? = null,
) : DialogControl()

fun ControlContainer.text(key: String, label: ChatComponents, initial: String? = null, width: Int? = null, block: Text.() -> Unit = {}) {
	val control = Text(key, width, label, initial = initial).apply(block)
	controls += control
}

fun ControlContainer.text(key: String, label: String, initial: String? = null, width: Int? = null, block: Text.() -> Unit = {}) =
	text(key, textComponent(label), initial, width, block)

fun Text.label(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	label = textComponent(text, color, block)
}
