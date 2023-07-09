package features.itemmodifiers.functions

import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class LootingEnchant(
	var count: NumberProvider,
	var limit: Int? = null,
) : ItemFunctionSurrogate
