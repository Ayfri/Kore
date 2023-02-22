package features.predicates

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class TableBonus(
	var enchantment: Argument.Enchantment,
	var chances: List<Float>,
) : Predicate
