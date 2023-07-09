package features.itemmodifiers.functions

import features.predicates.providers.IntOrIntNumberProvidersRange
import kotlinx.serialization.Serializable

@Serializable
data class LimitCount(
	val limit: IntOrIntNumberProvidersRange
) : ItemFunctionSurrogate
