package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.utils.toUniformProvider
import kotlinx.serialization.Serializable

/**
 * Sets a random number of dyes on the item. Mirrors `minecraft:set_random_dyes`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * Minecraft Wiki: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetRandomDyes(
	override var condition: PredicateCondition? = null,
	var numberOfDyes: ContextIntProvider,
) : ItemFunction()

/** Add a `set_random_dyes` step. */
fun ItemModifier.setRandomDyes(numberOfDyes: ContextIntProvider, block: SetRandomDyes.() -> Unit = {}) {
	modifiers += SetRandomDyes(numberOfDyes = numberOfDyes).apply(block)
}

/** Int convenience overload for `set_random_dyes`. */
fun ItemModifier.setRandomDyes(numberOfDyes: Int, block: SetRandomDyes.() -> Unit = {}) {
	modifiers += SetRandomDyes(numberOfDyes = constant(numberOfDyes)).apply(block)
}

/**
 * Add a `set_random_dyes` step drawing the number of dyes uniformly from [numberOfDyes], both ends included.
 *
 * ```
 * setRandomDyes(1..5)  // "number_of_dyes": {"type": "minecraft:uniform", "max": 5, "min": 1}
 * ```
 */
fun ItemModifier.setRandomDyes(numberOfDyes: IntRange, block: SetRandomDyes.() -> Unit = {}) =
	setRandomDyes(numberOfDyes.toUniformProvider(), block)
