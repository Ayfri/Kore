package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifierEntry
import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class SetEnchantments(
	var enchantments: Map<Argument.Enchantment, NumberProvider> = emptyMap(),
	var add: Boolean? = null,
) : ItemFunctionSurrogate

fun ItemModifierEntry.setEnchantments(add: Boolean? = null, enchantments: Map<Argument.Enchantment, NumberProvider>.() -> Unit = {}) {
	function = SetEnchantments(buildMap(enchantments), add)
}
