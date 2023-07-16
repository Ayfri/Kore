package features.worldgen.noisesettings.rules

import features.worldgen.noisesettings.rules.conditions.SurfaceRuleCondition
import kotlinx.serialization.Serializable

@Serializable
data class Condition(
	var ifTrue: SurfaceRuleCondition,
	var thenRun: SurfaceRule
) : SurfaceRule()

fun condition(condition: SurfaceRuleCondition, thenRun: SurfaceRule) = Condition(condition, thenRun)

fun condition(condition: SurfaceRuleCondition, thenBlock: MutableList<SurfaceRule>.() -> Unit) =
	Condition(condition, Sequence(buildList(thenBlock)))

fun condition(condition: SurfaceRuleCondition, vararg thenRules: SurfaceRule) =
	Condition(condition, Sequence(thenRules.toList()))
