package features.worldgen.placedfeature.modifiers

import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class Count(
	var count: IntProvider = constant(0),
) : PlacementModifier()

fun count(count: IntProvider = constant(0)) = Count(count)

fun MutableList<PlacementModifier>.count(count: IntProvider = constant(0)) {
	this += Count(count)
}
