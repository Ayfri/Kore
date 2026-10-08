package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.MaterialCondition
import kotlinx.serialization.Serializable

/**
 * Represents a condition that determines whether a material rule should be executed.
 *
 * @property ifTrue The condition that needs to be true for the rule to be executed.
 * @property thenRun The material rule to be executed if the condition is true.
 */
@Serializable
data class Condition(
	var ifTrue: MaterialCondition,
	var thenRun: MaterialRule,
) : MaterialRule()

/**
 * Appends a condition that associates a rule condition with a material rule.
 */
fun MaterialRulesScope.condition(condition: MaterialCondition, thenRun: MaterialRule) =
	apply { rules += Condition(condition, thenRun) }

/**
 * Appends a condition that associates a rule condition with the material rules appended in [thenBlock].
 *
 * A single rule is used as-is, several are wrapped in a [Sequence].
 */
fun MaterialRulesScope.condition(condition: MaterialCondition, thenBlock: MaterialRulesScope.() -> Unit) = apply {
	val thenRules = buildMaterialRules(thenBlock)
	rules += Condition(condition, thenRules.singleOrNull() ?: Sequence(thenRules))
}

/**
 * Appends a condition that associates a rule condition with a sequence of material rules.
 */
fun MaterialRulesScope.condition(condition: MaterialCondition, vararg thenRules: MaterialRule) =
	apply { rules += Condition(condition, Sequence(thenRules.toList())) }
