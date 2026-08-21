package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Matches one enchantment and its level, as an entry of the `minecraft:enchantments` and
 * `minecraft:stored_enchantments` component matchers.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class EnchantmentPredicate(
	var enchantments: InlinableList<EnchantmentOrTagArgument>? = null,
	var levels: IntRangeOrIntJson? = null,
)

/** Creates an [EnchantmentPredicate] matching any of [enchantments] at [levels]. */
fun enchantmentPredicate(vararg enchantments: EnchantmentOrTagArgument, levels: IntRangeOrIntJson? = null) =
	EnchantmentPredicate(enchantments.toList().ifEmpty { null }, levels)

/** Creates an [EnchantmentPredicate] matching any of [enchantments] at [levels]. */
fun enchantmentPredicate(enchantments: List<EnchantmentOrTagArgument>, levels: IntRangeOrIntJson? = null) =
	EnchantmentPredicate(enchantments, levels)
