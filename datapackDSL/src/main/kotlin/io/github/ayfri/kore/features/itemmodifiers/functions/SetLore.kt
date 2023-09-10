package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.chatcomponents.*
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetLore(
	override var conditions: PredicateAsList? = null,
	var entity: Source? = null,
	var lore: ChatComponents = textComponent(),
	var replace: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setLore(entity: Source? = null, replace: Boolean? = null, vararg lore: String, block: SetLore.() -> Unit = {}) {
	modifiers += SetLore(entity = entity, lore = ChatComponents(lore.map { text(it) }.toMutableList()), replace = replace).apply(block)
}

fun ItemModifier.setLore(
	entity: Source? = null,
	replace: Boolean? = null,
	text: String,
	color: Color? = null,
	block: PlainTextComponent.() -> Unit = {},
) = SetLore(entity = entity, lore = textComponent(text) {
	this.color = color
	block()
}, replace = replace).also { modifiers += it }

fun ItemModifier.setLore(
	entity: Source? = null,
	replace: Boolean? = null,
	components: ChatComponents,
	block: SetLore.() -> Unit = {},
) {
	modifiers += SetLore(entity = entity, lore = components, replace = replace).apply(block)
}

fun ItemModifier.setLore(
	entity: Source? = null,
	replace: Boolean? = null,
	component: ChatComponent,
	block: SetLore.() -> Unit = {},
) {
	modifiers += SetLore(entity = entity, lore = ChatComponents(component), replace = replace).apply(block)
}
