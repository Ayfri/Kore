package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Enchants the item with the specified enchantment level, optionally constrained to a set or tag.
 * Mirrors `minecraft:enchant_with_levels` and accepts a context int provider for the level.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class EnchantWithLevels(
	override var condition: PredicateCondition? = null,
	var levels: ContextIntProvider,
	var includeAdditionalCostComponent: Boolean? = null,
	var options: InlinableList<EnchantmentOrTagArgument>? = null,
) : ItemFunction()

/** Add an `enchant_with_levels` step. */
fun ItemModifier.enchantWithLevels(
	enchantments: List<EnchantmentOrTagArgument>? = null,
	levels: ContextIntProvider = constant(0),
	block: EnchantWithLevels.() -> Unit = {},
) {
	modifiers += EnchantWithLevels(options = enchantments, levels = levels).apply(block)
}

/** Vararg convenience overload for `enchant_with_levels`. */
fun ItemModifier.enchantWithLevels(
	vararg enchantments: EnchantmentOrTagArgument,
	levels: ContextIntProvider = constant(0),
	block: EnchantWithLevels.() -> Unit = {},
) {
	modifiers += EnchantWithLevels(options = enchantments.toList().ifEmpty { null }, levels = levels).apply(block)
}

/**
 * Add an `enchant_with_levels` step spending exactly [levels] levels.
 *
 * ```
 * enchantWithLevels(levels = 30)  // "levels": 30
 * ```
 */
fun ItemModifier.enchantWithLevels(
	enchantments: List<EnchantmentOrTagArgument>? = null,
	levels: Int,
	block: EnchantWithLevels.() -> Unit = {},
) = enchantWithLevels(enchantments, constant(levels), block)

/** Vararg convenience overload for `enchant_with_levels` spending exactly [levels] levels. */
fun ItemModifier.enchantWithLevels(
	vararg enchantments: EnchantmentOrTagArgument,
	levels: Int,
	block: EnchantWithLevels.() -> Unit = {},
) = enchantWithLevels(*enchantments, levels = constant(levels), block = block)

/** Configure the candidate enchantments list. */
fun EnchantWithLevels.enchantments(block: MutableList<EnchantmentOrTagArgument>.() -> Unit = {}) {
	options = buildList(block).ifEmpty { null }
}
