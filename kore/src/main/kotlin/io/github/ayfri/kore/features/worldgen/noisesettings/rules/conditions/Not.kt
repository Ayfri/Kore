package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

@Serializable
data class Not(
	var invert: SurfaceRuleCondition,
) : SurfaceRuleCondition()

fun not(invert: SurfaceRuleCondition) = Not(invert)

fun not(invert: () -> SurfaceRuleCondition) = Not(invert())
