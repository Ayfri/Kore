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
 * Appends a condition that associates a rule condition with a surface rule.
 */
fun MutableList<SurfaceRule>.condition(condition: SurfaceRuleCondition, thenRun: SurfaceRule) =
	apply { add(Condition(condition, thenRun)) }

/**
 * Appends a condition that associates a rule condition with a sequence of surface rules.
 */
fun MutableList<SurfaceRule>.condition(condition: SurfaceRuleCondition, thenBlock: MutableList<SurfaceRule>.() -> Unit) =
	apply { add(Condition(condition, Sequence(buildList(thenBlock)))) }

/**
 * Appends a condition that associates a rule condition with a sequence of surface rules.
 */
fun MutableList<SurfaceRule>.condition(condition: SurfaceRuleCondition, vararg thenRules: SurfaceRule) =
	apply { add(Condition(condition, Sequence(thenRules.toList()))) }
