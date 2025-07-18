package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

@Serializable
data class NoiseThreshold(
	var noise: NoiseArgument,
	var minThreshold: Double = 0.0,
	var maxThreshold: Double = 0.0,
) : SurfaceRuleCondition()

fun noiseThreshold(noise: NoiseArgument, minThreshold: Double = 0.0, maxThreshold: Double = 0.0) =
	NoiseThreshold(noise, minThreshold, maxThreshold)

fun noiseThreshold(noise: NoiseArgument, block: NoiseThreshold.() -> Unit) = NoiseThreshold(noise).apply(block)
