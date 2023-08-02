package features.worldgen.heightproviders

import features.worldgen.noisesettings.rules.conditions.HeightConstant
import features.worldgen.noisesettings.rules.conditions.absolute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:uniform")
data class UniformHeightProvider(
	var minInclusive: HeightConstant,
	var maxInclusive: HeightConstant,
) : HeightProvider()

fun uniformHeightProvider(
	minInclusive: HeightConstant,
	maxInclusive: HeightConstant,
) = UniformHeightProvider(minInclusive, maxInclusive)

fun uniformHeightProvider(
	minInclusive: Int,
	maxInclusive: Int,
) = uniformHeightProvider(absolute(minInclusive), absolute(maxInclusive))
