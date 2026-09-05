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
fun SurfaceRulesScope.condition(condition: SurfaceRuleCondition, thenRun: SurfaceRule) =
	apply { rules += Condition(condition, thenRun) }

/**
 * Appends a condition that associates a rule condition with the surface rules appended in [thenBlock].
 *
 * A single rule is used as-is, several are wrapped in a [Sequence].
 */
fun SurfaceRulesScope.condition(condition: SurfaceRuleCondition, thenBlock: SurfaceRulesScope.() -> Unit) = apply {
	val thenRules = buildSurfaceRules(thenBlock)
	rules += Condition(condition, thenRules.singleOrNull() ?: Sequence(thenRules))
}

/**
 * Appends a condition that associates a rule condition with a sequence of surface rules.
 */
fun SurfaceRulesScope.condition(condition: SurfaceRuleCondition, vararg thenRules: SurfaceRule) =
	apply { rules += Condition(condition, Sequence(thenRules.toList())) }
