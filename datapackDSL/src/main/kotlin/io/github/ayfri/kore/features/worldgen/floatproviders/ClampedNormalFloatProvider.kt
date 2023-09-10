package io.github.ayfri.kore.features.worldgen.floatproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:clamped_normal")
data class ClampedNormalFloatProvider(
	var mean: Float,
	var deviation: Float,
	var min: Float,
	var max: Float,
) : FloatProvider

fun clampedNormal(mean: Float, deviation: Float, min: Float, max: Float) = ClampedNormalFloatProvider(mean, deviation, min, max)
fun clampedNormalFloatProvider(mean: Float, deviation: Float, min: Float, max: Float) =
	ClampedNormalFloatProvider(mean, deviation, min, max)
