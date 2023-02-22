package features.predicates

import features.predicates.providers.IntOrNumberProvidersRange
import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class ValueCheck(
	var value: NumberProvider,
	var range: IntOrNumberProvidersRange
) : Predicate
