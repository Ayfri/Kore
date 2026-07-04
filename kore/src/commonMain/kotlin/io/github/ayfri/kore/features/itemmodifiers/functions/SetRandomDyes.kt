package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import kotlinx.serialization.Serializable

/**
 * Sets a random number of dyes on the item. Mirrors `minecraft:set_random_dyes`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * Minecraft Wiki: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetRandomDyes(
	override var conditions: PredicateAsList? = null,
	var numberOfDyes: NumberProvider,
) : ItemFunction()

/** Add a `set_random_dyes` step. */
fun ItemModifier.setRandomDyes(numberOfDyes: NumberProvider, block: SetRandomDyes.() -> Unit = {}) {
	modifiers += SetRandomDyes(numberOfDyes = numberOfDyes).apply(block)
}

/** Float convenience overload for `set_random_dyes`. */
fun ItemModifier.setRandomDyes(numberOfDyes: Float, block: SetRandomDyes.() -> Unit = {}) {
	modifiers += SetRandomDyes(numberOfDyes = constant(numberOfDyes)).apply(block)
}
