package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.SurfaceRulesScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

/**
 * Represents a condition that checks whether the value of [noise] falls between [minThreshold] and [maxThreshold].
 *
 * @property noise The noise sampled by the condition.
 * @property minThreshold The lower bound the noise value must reach for the condition to be true.
 * @property maxThreshold The upper bound the noise value must reach for the condition to be true.
 * @property is3d Whether [noise] is evaluated in 3D instead of the default 2D (X/Z) evaluation.
 */
@Serializable
data class NoiseThreshold(
	var noise: NoiseArgument,
	var minThreshold: Double = 0.0,
	var maxThreshold: Double = 0.0,
	var is3d: Boolean? = null,
) : SurfaceRuleCondition()

/**
 * Creates a [NoiseThreshold] condition, true when [noise] falls between [minThreshold] and [maxThreshold].
 */
fun SurfaceRulesScope.noiseThreshold(noise: NoiseArgument, minThreshold: Double = 0.0, maxThreshold: Double = 0.0) =
	NoiseThreshold(noise, minThreshold, maxThreshold)

/**
 * Creates a [NoiseThreshold] condition, true when [noise] falls between [minThreshold] and [maxThreshold].
 */
fun SurfaceRulesScope.noiseThreshold(noise: NoiseArgument, block: NoiseThreshold.() -> Unit) = NoiseThreshold(noise).apply(block)
