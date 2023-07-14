package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifier
import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class SetEnchantments(
	var enchantments: Map<Argument.Enchantment, NumberProvider> = emptyMap(),
	var add: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setEnchantments(add: Boolean? = null, enchantments: Map<Argument.Enchantment, NumberProvider>.() -> Unit = {}) =
	SetEnchantments(buildMap(enchantments), add).also { modifiers += it }
