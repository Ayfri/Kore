package features.itemmodifiers.functions

import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class SetCount(
	var count: NumberProvider,
	var add: Boolean? = null,
) : ItemFunctionSurrogate
