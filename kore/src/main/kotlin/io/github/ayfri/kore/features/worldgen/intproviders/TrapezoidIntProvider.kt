package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:trapezoid")
data class TrapezoidIntProvider(
	var min: Int,
	var max: Int,
	var plateau: Int,
) : IntProvider
