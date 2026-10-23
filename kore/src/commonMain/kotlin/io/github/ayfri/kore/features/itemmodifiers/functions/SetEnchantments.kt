package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import kotlinx.serialization.Serializable

/**
 * Sets enchantments on the item, optionally adding to existing ones. Mirrors `minecraft:set_enchantments`.
 * Keys are enchantments, values are context int providers for the level.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetEnchantments(
	override var condition: PredicateCondition? = null,
	var enchantments: Map<EnchantmentArgument, ContextIntProvider> = emptyMap(),
	var add: Boolean? = null,
) : ItemFunction()

/** Add a `set_enchantments` step and configure its map with a builder. */
fun ItemModifier.setEnchantments(add: Boolean? = null, enchantments: MutableMap<EnchantmentArgument, ContextIntProvider>.() -> Unit = {}) =
	SetEnchantments(enchantments = buildMap(enchantments), add = add).also { modifiers += it }
