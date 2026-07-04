package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

@Serializable
data class Water(
	var offset: Int = 0,
	var surfaceDepthMultiplier: Int = 0,
	var addStoneDepth: Boolean = false,
) : SurfaceRuleCondition()

fun water(
	offset: Int = 0,
	surfaceDepthMultiplier: Int = 0,
	addStoneDepth: Boolean = false,
) = Water(offset, surfaceDepthMultiplier, addStoneDepth)

fun water(
	block: Water.() -> Unit,
) = Water().apply(block)
