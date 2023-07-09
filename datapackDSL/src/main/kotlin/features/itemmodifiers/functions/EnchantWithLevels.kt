package features.itemmodifiers.functions

import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class EnchantWithLevels(
	var levels: NumberProvider,
	var treasure: Boolean? = null,
) : ItemFunctionSurrogate
