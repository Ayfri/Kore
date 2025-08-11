package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Applies explosion decay to reduce stack size based on explosion radius in the loot context.
 * Mirrors vanilla `minecraft:explosion_decay`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class ExplosionDecay(override var conditions: PredicateAsList? = null) : ItemFunction()

/** Add an `explosion_decay` step. */
fun ItemModifier.explosionDecay(block: ExplosionDecay.() -> Unit = {}) {
	modifiers += ExplosionDecay().apply(block)
}
