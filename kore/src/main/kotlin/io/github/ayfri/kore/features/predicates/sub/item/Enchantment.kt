package io.github.ayfri.kore.features.predicates.sub.item

import io.github.ayfri.kore.arguments.types.EnchantmentOrTagArgument
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Enchantment(
	var enchantments: InlinableList<EnchantmentOrTagArgument>? = null,
	var levels: IntRangeOrIntJson? = null,
)

fun enchantment(vararg enchantments: EnchantmentOrTagArgument, levels: IntRangeOrIntJson? = null) =
	Enchantment(enchantments.toList(), levels)

fun enchantment(enchantments: List<EnchantmentOrTagArgument>, levels: IntRangeOrIntJson? = null) = Enchantment(enchantments, levels)
