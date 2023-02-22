package features.advancements.types

import arguments.Argument
import features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Enchantment(
	var enchantment: Argument.Enchantment? = null,
	var levels: IntRangeOrIntJson? = null,
)

fun enchantment(enchantment: Argument.Enchantment? = null, levels: IntRangeOrIntJson? = null) = Enchantment(enchantment, levels)
