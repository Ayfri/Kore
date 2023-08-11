package features.worldgen.configuredfeature.configurations

import features.worldgen.configuredfeature.Target
import kotlinx.serialization.Serializable

@Serializable
data class ScatteredOre(
	var size: Int = 0,
	var discardChanceOnAirExposure: Double = 0.0,
	var targets: List<Target> = emptyList(),
) : FeatureConfig()

fun scatteredOre(
	size: Int = 0,
	discardChanceOnAirExposure: Double = 0.0,
	targets: List<Target> = emptyList(),
	block: ScatteredOre.() -> Unit = {},
) = ScatteredOre(size, discardChanceOnAirExposure, targets).apply(block)

fun scatteredOre(
	size: Int = 0,
	discardChanceOnAirExposure: Double = 0.0,
	vararg targets: Target,
) = ScatteredOre(size, discardChanceOnAirExposure, targets.toList())
