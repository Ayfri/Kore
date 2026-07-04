package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import kotlinx.serialization.Serializable

/**
 * Adjusts stack size based on the level of a specific enchantment on the killer entity.
 * Mirrors vanilla `minecraft:enchanted_count_increase`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class EnchantedCountIncrease(
	override var conditions: PredicateAsList? = null,
	var enchantment: EnchantmentArgument,
	var count: NumberProvider,
	var limit: Int? = null,
) : ItemFunction()

/** Add an `enchanted_count_increase` step. */
fun ItemModifier.enchantedCountIncrease(
	enchantment: EnchantmentArgument,
	count: NumberProvider,
	limit: Int? = null,
	block: EnchantedCountIncrease.() -> Unit = {},
) {
	modifiers += EnchantedCountIncrease(enchantment = enchantment, count = count, limit = limit).apply(block)
}

/** Float convenience overload for `enchanted_count_increase`. */
fun ItemModifier.enchantedCountIncrease(
	enchantment: EnchantmentArgument,
	count: Float,
	limit: Int? = null,
	block: EnchantedCountIncrease.() -> Unit = {},
) {
	modifiers += EnchantedCountIncrease(enchantment = enchantment, count = constant(count), limit = limit).apply(block)
}
