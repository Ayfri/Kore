package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import io.github.ayfri.kore.serializers.LowercaseSerializer
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

fun PlacedFeature.environmentScan(
	directionOfSearchDirection: SearchDirection,
	maxSteps: IntProvider = constant(0),
	targetCondition: BlockPredicate = True,
	allowedSearchCondition: BlockPredicate? = null,
	block: EnvironmentScan.() -> Unit = {},
) {
	placementModifiers += EnvironmentScan(directionOfSearchDirection, maxSteps, targetCondition, allowedSearchCondition).apply(block)
}
