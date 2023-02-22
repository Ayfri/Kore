package features.predicates

import features.predicates.providers.IntOrNumberProvidersRange
import kotlinx.serialization.Serializable

@Serializable
data class TimeCheck(
	var value: IntOrNumberProvidersRange,
	var period: Int? = null
) : Predicate
