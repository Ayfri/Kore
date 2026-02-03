package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Enchants an item with one randomly selected enchantment, optionally constrained to a set
 * or a tag, and optionally only compatible ones. Mirrors `minecraft:enchant_randomly`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class EnchantRandomly(
	override var conditions: PredicateAsList? = null,
	var options: InlinableList<EnchantmentOrTagArgument> = emptyList(),
	var onlyCompatible: Boolean? = null,
) : ItemFunction()

/** Add an `enchant_randomly` step. */
fun ItemModifier.enchantRandomly(
	enchantments: List<EnchantmentOrTagArgument> = emptyList(),
	onlyCompatible: Boolean? = null,
	block: EnchantRandomly.() -> Unit = {},
) {
	modifiers += EnchantRandomly(options = enchantments, onlyCompatible = onlyCompatible).apply(block)
}

/** Vararg convenience overload for `enchant_randomly`. */
fun ItemModifier.enchantRandomly(
	vararg enchantments: EnchantmentOrTagArgument,
	onlyCompatible: Boolean? = null, block: EnchantRandomly.() -> Unit = {},
) {
	modifiers += EnchantRandomly(options = enchantments.toList(), onlyCompatible = onlyCompatible).apply(block)
}

/** Configure the candidate enchantments list. */
fun EnchantRandomly.enchantments(block: MutableList<EnchantmentOrTagArgument>.() -> Unit = {}) {
	options = buildList(block)
}
