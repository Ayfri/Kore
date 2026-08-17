package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.SurfaceRulesScope
import kotlinx.serialization.Serializable

@Serializable
data class Not(
	var invert: SurfaceRuleCondition,
) : SurfaceRuleCondition()

fun SurfaceRulesScope.not(invert: SurfaceRuleCondition) = Not(invert)

fun SurfaceRulesScope.not(invert: SurfaceRulesScope.() -> SurfaceRuleCondition) = Not(invert())
