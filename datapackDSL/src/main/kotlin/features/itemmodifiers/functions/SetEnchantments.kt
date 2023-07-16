package features.itemmodifiers.functions

import arguments.types.resources.EnchantmentArgument
import features.itemmodifiers.ItemModifier
import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class SetEnchantments(
	var enchantments: Map<EnchantmentArgument, NumberProvider> = emptyMap(),
	var add: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setEnchantments(add: Boolean? = null, enchantments: Map<EnchantmentArgument, NumberProvider>.() -> Unit = {}) =
	SetEnchantments(buildMap(enchantments), add).also { modifiers += it }
