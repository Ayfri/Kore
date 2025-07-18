package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import kotlinx.serialization.Serializable

@Serializable
data class SetEnchantments(
	override var conditions: PredicateAsList? = null,
	var enchantments: Map<EnchantmentArgument, NumberProvider> = emptyMap(),
	var add: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setEnchantments(add: Boolean? = null, enchantments: MutableMap<EnchantmentArgument, NumberProvider>.() -> Unit = {}) =
	SetEnchantments(enchantments = buildMap(enchantments), add = add).also { modifiers += it }
