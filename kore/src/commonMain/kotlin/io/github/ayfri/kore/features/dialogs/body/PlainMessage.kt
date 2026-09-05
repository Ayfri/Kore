package io.github.ayfri.kore.features.dialogs.body

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import kotlinx.serialization.Serializable

@Serializable
data class PlainMessage(
	var contents: ChatComponents,
	var width: Int? = null,
) : DialogBody()

fun BodyContainer.plainMessage(contents: ChatComponents, block: PlainMessage.() -> Unit = {}) = apply {
	val body = PlainMessage(contents).apply(block)
	bodies += body
}

fun BodyContainer.plainMessage(contents: String, block: PlainMessage.() -> Unit = {}) =
	plainMessage(textComponent(contents), block)

fun PlainMessage.contents(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	contents = textComponent(text, color, block)
}
