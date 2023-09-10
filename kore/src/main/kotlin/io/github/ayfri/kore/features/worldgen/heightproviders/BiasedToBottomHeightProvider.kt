package io.github.ayfri.kore.features.worldgen.heightproviders

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.HeightConstant
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.absolute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:biased_to_bottom")
data class BiasedToBottomHeightProvider(
	var minInclusive: HeightConstant,
	var maxInclusive: HeightConstant,
	var inner: Int? = null,
) : HeightProvider

fun biasedToBottomHeightProvider(
	minInclusive: HeightConstant,
	maxInclusive: HeightConstant,
	inner: Int? = null,
) = BiasedToBottomHeightProvider(minInclusive, maxInclusive, inner)

fun biasedToBottomHeightProvider(
	minInclusive: Int,
	maxInclusive: Int,
	inner: Int? = null,
) = biasedToBottomHeightProvider(absolute(minInclusive), absolute(maxInclusive), inner)
