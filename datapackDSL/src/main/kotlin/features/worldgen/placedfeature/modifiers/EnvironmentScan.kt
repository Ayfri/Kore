package features.worldgen.placedfeature.modifiers

import features.worldgen.blockpredicate.BlockPredicate
import features.worldgen.blockpredicate.True
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable
data class EnvironmentScan(
	var directionOfSearchDirection: SearchDirection,
	var maxSteps: IntProvider = constant(0),
	var targetCondition: BlockPredicate = True,
	var allowedSearchCondition: BlockPredicate? = null,
) : PlacementModifier()

@Serializable(with = SearchDirection.Companion.DirectionSerializer::class)
enum class SearchDirection {
	UP,
	DOWN;

	companion object {
		data object DirectionSerializer : LowercaseSerializer<SearchDirection>(entries)
	}
}
