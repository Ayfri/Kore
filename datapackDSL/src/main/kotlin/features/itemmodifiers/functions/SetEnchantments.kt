package features.itemmodifiers.functions

import arguments.types.resources.EnchantmentArgument
import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class SetEnchantments(
	override var conditions: PredicateAsList? = null,
	var enchantments: Map<EnchantmentArgument, NumberProvider> = emptyMap(),
	var add: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setEnchantments(add: Boolean? = null, enchantments: Map<EnchantmentArgument, NumberProvider>.() -> Unit = {}) =
	SetEnchantments(enchantments = buildMap(enchantments), add = add).also { modifiers += it }
