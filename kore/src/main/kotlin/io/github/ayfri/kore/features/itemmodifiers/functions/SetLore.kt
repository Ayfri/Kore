package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.chatcomponents.*
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.Mode
import io.github.ayfri.kore.features.itemmodifiers.types.ModeHandler
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetLore(
	override var conditions: PredicateAsList? = null,
	var entity: Source? = null,
	var lore: ChatComponents = textComponent(),
) : ItemFunction(), ModeHandler {
	@Serializable
	override var mode: Mode = Mode.REPLACE_ALL

	@Serializable
	override var offset: Int? = null

	@Serializable
	override var size: Int? = null
}

fun ItemModifier.setLore(entity: Source? = null, vararg lore: String, block: SetLore.() -> Unit = {}) {
	modifiers += SetLore(entity = entity, lore = ChatComponents(lore.map(::text).toMutableList())).apply(block)
}

fun ItemModifier.setLore(
	entity: Source? = null,
	text: String,
	color: Color? = null,
	block: PlainTextComponent.() -> Unit = {},
) = SetLore(entity = entity, lore = textComponent(text) {
	this.color = color
	block()
}).also { modifiers += it }

fun ItemModifier.setLore(
	entity: Source? = null,
	components: ChatComponents,
	block: SetLore.() -> Unit = {},
) {
	modifiers += SetLore(entity = entity, lore = components).apply(block)
}

fun ItemModifier.setLore(
	entity: Source? = null,
	component: ChatComponent,
	block: SetLore.() -> Unit = {},
) {
	modifiers += SetLore(entity = entity, lore = ChatComponents(component)).apply(block)
}

fun SetLore.lore(vararg lore: ChatComponent) = apply {
	lore.forEach { this.lore += it }
}

fun SetLore.lore(lore: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	this.lore += text(lore, color, block)
}
