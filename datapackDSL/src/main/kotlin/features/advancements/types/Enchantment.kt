package features.advancements.types

import arguments.types.resources.EnchantmentArgument
import features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Enchantment(
	var enchantment: EnchantmentArgument? = null,
	var levels: IntRangeOrIntJson? = null,
)

fun enchantment(enchantment: EnchantmentArgument? = null, levels: IntRangeOrIntJson? = null) = Enchantment(enchantment, levels)
