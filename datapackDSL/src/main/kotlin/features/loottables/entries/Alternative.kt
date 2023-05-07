package features.loottables.entries

import features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

@Serializable
data class Alternative(
	var children: List<LootEntry>,
	var conditions: List<PredicateCondition>? = null,
) : LootEntry
