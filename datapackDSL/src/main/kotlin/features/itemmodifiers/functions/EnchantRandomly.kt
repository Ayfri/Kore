package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class EnchantRandomly(
	var enchantments: List<Argument.Enchantment> = emptyList(),
) : ItemFunction()

fun ItemModifier.enchantRandomly(enchantments: List<Argument.Enchantment> = emptyList(), block: EnchantRandomly.() -> Unit = {}) {
	modifiers += EnchantRandomly(enchantments).apply(block)
}

fun ItemModifier.enchantRandomly(vararg enchantments: Argument.Enchantment, block: EnchantRandomly.() -> Unit = {}) {
	modifiers += EnchantRandomly(enchantments.toList()).apply(block)
}

fun EnchantRandomly.enchantments(block: MutableList<Argument.Enchantment>.() -> Unit = {}) {
	enchantments = buildList(block)
}
