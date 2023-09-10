package io.github.ayfri.kore.features.worldgen.floatproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:uniform")
data class UniformFloatProvider(
	var minInclusive: Float,
	var maxExclusive: Float,
) : FloatProvider

fun uniform(minInclusive: Float, maxExclusive: Float) = UniformFloatProvider(minInclusive, maxExclusive)
fun uniformFloatProvider(minInclusive: Float, maxExclusive: Float) = UniformFloatProvider(minInclusive, maxExclusive)
