package features.worldgen.noisesettings.rules.conditions

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class NoiseTreshold(
	var noise: Argument.Noise,
	var minThreshold: Double = 0.0,
	var maxThreshold: Double = 0.0,
) : SurfaceRuleCondition()

fun noiseTreshold(noise: Argument.Noise, minThreshold: Double = 0.0, maxThreshold: Double = 0.0) =
	NoiseTreshold(noise, minThreshold, maxThreshold)

fun noiseTreshold(noise: Argument.Noise, block: NoiseTreshold.() -> Unit) = NoiseTreshold(noise).apply(block)
