package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.chatcomponents.*
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.Mode
import io.github.ayfri.kore.features.itemmodifiers.types.ModeHandler
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Sets or edits item lore with list-operation support (mode/offset/size). Mirrors `minecraft:set_lore`.
 * The `entity` source can be used to resolve text from an entity context.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
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

/** Add a `set_lore` step with a list of strings. */
fun ItemModifier.setLore(entity: Source? = null, vararg lore: String, block: SetLore.() -> Unit = {}) {
	modifiers += SetLore(entity = entity, lore = ChatComponents(lore.map(::text).toMutableList())).apply(block)
}

/** Add a `set_lore` step with a single string. */
fun ItemModifier.setLore(
	entity: Source? = null,
	text: String,
	color: Color? = null,
	block: PlainTextComponent.() -> Unit = {},
) = SetLore(entity = entity, lore = textComponent(text) {
	this.color = color
	block()
}).also { modifiers += it }

/** Add a `set_lore` step with a list of chat components. */
fun ItemModifier.setLore(
	entity: Source? = null,
	components: ChatComponents,
	block: SetLore.() -> Unit = {},
) {
	modifiers += SetLore(entity = entity, lore = components).apply(block)
}

/** Add a `set_lore` step with a single chat component. */
fun ItemModifier.setLore(
	entity: Source? = null,
	component: ChatComponent,
	block: SetLore.() -> Unit = {},
) {
	modifiers += SetLore(entity = entity, lore = ChatComponents(component)).apply(block)
}

/** Append multiple chat components to the lore. */
fun SetLore.lore(vararg lore: ChatComponent) = apply {
	lore.forEach { this.lore += it }
}

/** Append a single string to the lore. */
fun SetLore.lore(lore: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	this.lore += text(lore, color, block)
}
