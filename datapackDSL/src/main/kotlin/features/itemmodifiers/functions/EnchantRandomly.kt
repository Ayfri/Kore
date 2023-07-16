package features.itemmodifiers.functions

import arguments.types.resources.EnchantmentArgument
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class EnchantRandomly(
	var enchantments: List<EnchantmentArgument> = emptyList(),
) : ItemFunction()

fun ItemModifier.enchantRandomly(enchantments: List<EnchantmentArgument> = emptyList(), block: EnchantRandomly.() -> Unit = {}) {
	modifiers += EnchantRandomly(enchantments).apply(block)
}

fun ItemModifier.enchantRandomly(vararg enchantments: EnchantmentArgument, block: EnchantRandomly.() -> Unit = {}) {
	modifiers += EnchantRandomly(enchantments.toList()).apply(block)
}

fun EnchantRandomly.enchantments(block: MutableList<EnchantmentArgument>.() -> Unit = {}) {
	enchantments = buildList(block)
}
