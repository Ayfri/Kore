package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.colors.Color
import kotlinx.serialization.Serializable

/**
 * A `minecraft:text` component that renders a plain string. The content is stored in the inherited [ChatComponent.text] field.
 *
 * Docs: [Text component format - Plain text](https://minecraft.wiki/w/Text_component_format#Plain_text)
 */
@Serializable
class PlainTextComponent : ChatComponent(), SimpleComponent {
	override val type = ChatComponentType.TEXT

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		return super.equals(other)
	}

}

/** Creates a [PlainTextComponent] with the given [text] and optional [color]. */
fun text(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = PlainTextComponent().apply {
	this.text = text
	this.color = color
	block()
}
