package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponent
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
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
