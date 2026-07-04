package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import kotlinx.serialization.Serializable

/**
 * Sets enchantments on the item, optionally adding to existing ones. Mirrors `minecraft:set_enchantments`.
 * Keys are enchantments, values are number providers for the level.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetEnchantments(
	override var conditions: PredicateAsList? = null,
	var enchantments: Map<EnchantmentArgument, NumberProvider> = emptyMap(),
	var add: Boolean? = null,
) : ItemFunction()

/** Add a `set_enchantments` step and configure its map with a builder. */
fun ItemModifier.setEnchantments(add: Boolean? = null, enchantments: MutableMap<EnchantmentArgument, NumberProvider>.() -> Unit = {}) =
	SetEnchantments(enchantments = buildMap(enchantments), add = add).also { modifiers += it }
