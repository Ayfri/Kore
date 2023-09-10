package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.colors.Color
import kotlinx.serialization.Serializable

@Serializable
class PlainTextComponent : ChatComponent(), SimpleComponent {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		return super.equals(other)
	}

	override fun hashCode() = super.hashCode()
}

fun text(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = PlainTextComponent().apply {
	this.text = text
	this.color = color
	block()
}
