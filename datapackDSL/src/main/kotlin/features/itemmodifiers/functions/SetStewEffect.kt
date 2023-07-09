package features.itemmodifiers.functions

import arguments.Argument
import features.predicates.providers.NumberProvider
import features.predicates.providers.constant
import kotlinx.serialization.Serializable

@Serializable
data class SetStewEffect(
	val effects: List<PotionEffect>
) : ItemFunctionSurrogate

@Serializable
data class PotionEffect(
	var type: Argument.Potion,
	var duration: NumberProvider = constant(0f)
)
