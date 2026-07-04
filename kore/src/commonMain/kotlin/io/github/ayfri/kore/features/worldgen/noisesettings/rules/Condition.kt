package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.SurfaceRuleCondition
import kotlinx.serialization.Serializable

/**
 * Represents a condition that determines whether a surface rule should be executed.
 *
 * @property ifTrue The condition that needs to be true for the rule to be executed.
 * @property thenRun The surface rule to be executed if the condition is true.
 */
@Serializable
data class Condition(
	var ifTrue: SurfaceRuleCondition,
	var thenRun: SurfaceRule,
) : SurfaceRule()

/**
 * Creates a condition that associates a rule condition with a surface rule.
 */
fun condition(condition: SurfaceRuleCondition, thenRun: SurfaceRule) = Condition(condition, thenRun)

/**
 * Creates a condition that associates a rule condition with a sequence of surface rules.
 */
fun condition(condition: SurfaceRuleCondition, thenBlock: MutableList<SurfaceRule>.() -> Unit) =
	Condition(condition, Sequence(buildList(thenBlock)))

/**
 * Creates a condition that associates a rule condition with a sequence of surface rules.
 */
fun condition(condition: SurfaceRuleCondition, vararg thenRules: SurfaceRule) = Condition(condition, Sequence(thenRules.toList()))
