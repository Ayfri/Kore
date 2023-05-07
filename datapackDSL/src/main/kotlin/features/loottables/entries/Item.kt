package features.loottables.entries

import arguments.Argument
import features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

@Serializable
data class Item(
	var name: Argument.Item,
	var conditions: List<PredicateCondition>? = null,
	var functions: List<String>? = null,
	var weight: Int? = null,
	var quality: Int? = null,
) : LootEntry
