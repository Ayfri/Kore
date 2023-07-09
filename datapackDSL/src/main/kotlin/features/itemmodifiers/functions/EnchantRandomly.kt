package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data class EnchantRandomly(
	val enchantments: List<Argument.Enchantment> = emptyList(),
) : ItemFunctionSurrogate

fun ItemModifierEntry.enchantRandomly(enchantments: List<Argument.Enchantment> = emptyList()) {
	function = EnchantRandomly(enchantments)
}

fun ItemModifierEntry.enchantRandomly(vararg enchantments: Argument.Enchantment) {
	function = EnchantRandomly(enchantments.toList())
}

fun ItemModifierEntry.enchantRandomly(enchantments: MutableList<Argument.Enchantment>.() -> Unit) {
	function = EnchantRandomly(buildList(enchantments))
}
