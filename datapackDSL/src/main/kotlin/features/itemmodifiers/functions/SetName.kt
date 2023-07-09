package features.itemmodifiers.functions

import arguments.ChatComponents
import arguments.Color
import arguments.chatcomponents.ChatComponent
import arguments.chatcomponents.textComponent
import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data class SetName(
	var entity: Source? = null,
	var name: ChatComponents,
) : ItemFunctionSurrogate

fun ItemModifierEntry.setName(entity: Source? = null, name: String, color: Color? = null, block: ChatComponent.() -> Unit = {}) {
	function = SetName(entity, textComponent(name) {
		this.color = color
		block()
	})
}

fun ItemModifierEntry.setName(entity: Source? = null, name: ChatComponent) {
	function = SetName(entity, ChatComponents(name))
}
