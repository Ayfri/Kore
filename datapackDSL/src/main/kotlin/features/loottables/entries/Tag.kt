package features.loottables.entries

import arguments.Argument
import features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
	var name: Argument.Tag,
	var conditions: List<PredicateCondition>? = null,
	var expand: Boolean? = null,
	var functions: List<String>? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry
