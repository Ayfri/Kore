package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import kotlinx.serialization.Serializable

/**
 * References another item modifier by name. Mirrors `minecraft:reference`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class Reference(
	override var conditions: PredicateAsList? = null,
	val name: String,
) : ItemFunction()

/** Add a `reference` step using a raw string name. */
fun ItemModifier.reference(name: String, block: Reference.() -> Unit = {}) {
	modifiers += Reference(name = name).apply(block)
}

/** Add a `reference` step using an [ItemModifierArgument]. */
fun ItemModifier.reference(itemModifier: ItemModifierArgument, block: Reference.() -> Unit = {}) {
	modifiers += Reference(name = itemModifier.asString()).apply(block)
}
