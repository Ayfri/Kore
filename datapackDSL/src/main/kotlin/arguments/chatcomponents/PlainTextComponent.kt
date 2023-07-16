package arguments.chatcomponents

import arguments.colors.Color
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

fun text(text: String = "", block: PlainTextComponent.() -> Unit = {}) = PlainTextComponent().apply {
	this.text = text
	block()
}

fun text(text: String = "", color: Color, block: PlainTextComponent.() -> Unit = {}) = text(text) {
	this.color = color
	block()
}
