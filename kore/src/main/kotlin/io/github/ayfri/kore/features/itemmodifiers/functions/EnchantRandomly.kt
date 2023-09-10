package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class EnchantRandomly(
	override var conditions: PredicateAsList? = null,
	var enchantments: List<EnchantmentArgument> = emptyList(),
) : ItemFunction()

fun ItemModifier.enchantRandomly(enchantments: List<EnchantmentArgument> = emptyList(), block: EnchantRandomly.() -> Unit = {}) {
	modifiers += EnchantRandomly(enchantments = enchantments).apply(block)
}

fun ItemModifier.enchantRandomly(vararg enchantments: EnchantmentArgument, block: EnchantRandomly.() -> Unit = {}) {
	modifiers += EnchantRandomly(enchantments = enchantments.toList()).apply(block)
}

fun EnchantRandomly.enchantments(block: MutableList<EnchantmentArgument>.() -> Unit = {}) {
	enchantments = buildList(block)
}
