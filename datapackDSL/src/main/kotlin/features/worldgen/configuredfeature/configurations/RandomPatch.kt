package features.worldgen.configuredfeature.configurations

import arguments.types.resources.worldgen.PlacedFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class RandomPatch(
	var tries: Int? = null,
	var xzSpread: Int? = null,
	var ySpread: Int? = null,
	var placedFeature: PlacedFeatureArgument,
) : FeatureConfig()

fun randomPatch(
	tries: Int? = null,
	xzSpread: Int? = null,
	ySpread: Int? = null,
	placedFeature: PlacedFeatureArgument,
	block: RandomPatch.() -> Unit = {},
) = RandomPatch(tries, xzSpread, ySpread, placedFeature).apply(block)

fun randomPatch(
	placedFeature: PlacedFeatureArgument,
	block: RandomPatch.() -> Unit,
) = RandomPatch(null, null, null, placedFeature).apply(block)
