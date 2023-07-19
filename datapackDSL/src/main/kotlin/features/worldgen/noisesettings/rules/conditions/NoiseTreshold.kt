package features.worldgen.noisesettings.rules.conditions

import arguments.types.resources.worldgen.NoiseArgument
import kotlinx.serialization.Serializable

@Serializable
data class NoiseTreshold(
	var noise: NoiseArgument,
	var minThreshold: Double = 0.0,
	var maxThreshold: Double = 0.0,
) : SurfaceRuleCondition()

fun noiseTreshold(noise: NoiseArgument, minThreshold: Double = 0.0, maxThreshold: Double = 0.0) =
	NoiseTreshold(noise, minThreshold, maxThreshold)

fun noiseTreshold(noise: NoiseArgument, block: NoiseTreshold.() -> Unit) = NoiseTreshold(noise).apply(block)
