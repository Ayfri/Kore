package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import kotlinx.serialization.Serializable

@Serializable
data class EnchantedCountIncrease(
	override var conditions: PredicateAsList? = null,
	var enchantment: EnchantmentArgument,
	var count: NumberProvider,
	var limit: Int? = null,
) : ItemFunction()

fun ItemModifier.enchantedCountIncrease(
	enchantment: EnchantmentArgument,
	count: NumberProvider,
	limit: Int? = null,
	block: EnchantedCountIncrease.() -> Unit = {},
) {
	modifiers += EnchantedCountIncrease(enchantment = enchantment, count = count, limit = limit).apply(block)
}

fun ItemModifier.enchantedCountIncrease(
	enchantment: EnchantmentArgument,
	count: Float,
	limit: Int? = null,
	block: EnchantedCountIncrease.() -> Unit = {},
) {
	modifiers += EnchantedCountIncrease(enchantment = enchantment, count = constant(count), limit = limit).apply(block)
}
