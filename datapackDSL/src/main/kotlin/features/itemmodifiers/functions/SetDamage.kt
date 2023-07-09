package features.itemmodifiers.functions

import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class SetDamage(
	var damage: NumberProvider,
	var add: Boolean? = null,
) : ItemFunctionSurrogate
