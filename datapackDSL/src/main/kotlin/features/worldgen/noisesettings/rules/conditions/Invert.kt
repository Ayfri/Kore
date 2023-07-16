package features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

@Serializable
data class Invert(
	var invert: SurfaceRuleCondition
) : SurfaceRuleCondition()

fun invert(invert: SurfaceRuleCondition) = Invert(invert)

fun invert(invert: () -> SurfaceRuleCondition) = Invert(invert())
