package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.types.PotionArgument
import kotlinx.serialization.Serializable

/**
 * Sets the potion id on potion-like items. Mirrors `minecraft:set_potion`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetPotion(
	override var conditions: PredicateAsList? = null,
	val potion: PotionArgument,
) : ItemFunction()

/** Add a `set_potion` step. */
fun ItemModifier.setPotion(potion: PotionArgument, block: SetPotion.() -> Unit = {}) {
	modifiers += SetPotion(potion = potion).apply(block)
}
