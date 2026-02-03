package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Enchants the item with the specified enchantment level, optionally constrained to a set or tag.
 * Mirrors `minecraft:enchant_with_levels` and accepts a number provider for the level.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class EnchantWithLevels(
	override var conditions: PredicateAsList? = null,
	var options: InlinableList<EnchantmentOrTagArgument> = emptyList(),
	var levels: NumberProvider,
) : ItemFunction()

/** Add an `enchant_with_levels` step. */
fun ItemModifier.enchantWithLevels(
	enchantments: List<EnchantmentOrTagArgument> = emptyList(),
	levels: NumberProvider = constant(0f),
	block: EnchantWithLevels.() -> Unit = {},
) {
	modifiers += EnchantWithLevels(options = enchantments, levels = levels).apply(block)
}

/** Vararg convenience overload for `enchant_with_levels`. */
fun ItemModifier.enchantWithLevels(
	vararg enchantments: EnchantmentOrTagArgument,
	levels: NumberProvider = constant(0f),
	block: EnchantWithLevels.() -> Unit = {},
) {
	modifiers += EnchantWithLevels(options = enchantments.toList(), levels = levels).apply(block)
}

/** Configure the candidate enchantments list. */
fun EnchantWithLevels.enchantments(block: MutableList<EnchantmentOrTagArgument>.() -> Unit = {}) {
	options = buildList(block)
}
