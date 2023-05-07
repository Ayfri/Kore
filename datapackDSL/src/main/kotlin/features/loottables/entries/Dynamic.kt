package features.loottables.entries

import features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

@Serializable
data class Dynamic(
	var name: String,
	var conditions: List<PredicateCondition>? = null,
	var functions: List<String>? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry
