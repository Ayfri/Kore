package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Enchantment(
	var enchantment: EnchantmentArgument? = null,
	var levels: IntRangeOrIntJson? = null,
)

fun enchantment(enchantment: EnchantmentArgument? = null, levels: IntRangeOrIntJson? = null) = Enchantment(enchantment, levels)
