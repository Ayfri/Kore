package features.worldgen.heightproviders

import features.worldgen.noisesettings.rules.conditions.HeightConstant
import features.worldgen.noisesettings.rules.conditions.absolute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:very_biased_to_bottom")
data class VeryBiasedToBottomHeightProvider(
	var minInclusive: HeightConstant,
	var maxInclusive: HeightConstant,
	var inner: Int? = null,
) : HeightProvider

fun veryBiasedToBottomHeightProvider(
	minInclusive: HeightConstant,
	maxInclusive: HeightConstant,
	inner: Int? = null,
) = VeryBiasedToBottomHeightProvider(minInclusive, maxInclusive, inner)

fun veryBiasedToBottomHeightProvider(
	minInclusive: Int,
	maxInclusive: Int,
	inner: Int? = null,
) = veryBiasedToBottomHeightProvider(absolute(minInclusive), absolute(maxInclusive), inner)
