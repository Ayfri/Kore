package features.worldgen.heightproviders

import features.worldgen.noisesettings.rules.conditions.HeightConstant
import features.worldgen.noisesettings.rules.conditions.absolute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:trapezoid")
data class TrapezoidHeightProvider(
	var minInclusive: HeightConstant,
	var maxInclusive: HeightConstant,
	var plateau: Int? = null,
) : HeightProvider

fun trapezoidHeightProvider(
	minInclusive: HeightConstant,
	maxInclusive: HeightConstant,
	plateau: Int? = null,
) = TrapezoidHeightProvider(minInclusive, maxInclusive, plateau)

fun trapezoidHeightProvider(
	minInclusive: Int,
	maxInclusive: Int,
	plateau: Int? = null,
) = TrapezoidHeightProvider(absolute(minInclusive), absolute(maxInclusive), plateau)
