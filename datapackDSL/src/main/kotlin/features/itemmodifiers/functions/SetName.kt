package features.itemmodifiers.functions

import arguments.chatcomponents.ChatComponent
import arguments.chatcomponents.ChatComponents
import arguments.chatcomponents.textComponent
import arguments.colors.Color
import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetName(
	override var conditions: PredicateAsList? = null,
	var entity: Source? = null,
	var name: ChatComponents,
) : ItemFunction()

fun ItemModifier.setName(entity: Source? = null, name: String, color: Color? = null, block: ChatComponent.() -> Unit = {}) =
	SetName(entity = entity, name = textComponent(name) {
		this.color = color
		block()
	}).also { modifiers += it }

fun ItemModifier.setName(entity: Source? = null, name: ChatComponent, block: SetName.() -> Unit = {}) {
	modifiers += SetName(entity = entity, name = ChatComponents(name)).apply(block)
}
