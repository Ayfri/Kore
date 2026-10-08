package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.features.worldgen.noisesettings.NoiseSettings
import kotlinx.serialization.Serializable

/**
 * Represents a sequence of material rules.
 *
 * @property sequence The list of material rules in the sequence.
 */
@Serializable
data class Sequence(
	var sequence: List<MaterialRule>,
) : MaterialRule()

/**
 * Sets the material rules for the noise settings.
 */
fun NoiseSettings.materialRules(block: MaterialRulesScope.() -> Unit) {
	materialRule = Sequence(buildMaterialRules(block))
}

/**
 * Sets the material rules for the noise settings.
 */
fun NoiseSettings.materialRules(vararg rules: MaterialRule) {
	materialRule = Sequence(rules.toList())
}

@Deprecated("Renamed to materialRule, like the game's field since 26.3.", ReplaceWith("materialRule"))
var NoiseSettings.surfaceRule
	get() = materialRule
	set(value) {
		materialRule = value
	}

@Deprecated("Renamed to materialRules, like the game's field since 26.3.", ReplaceWith("materialRules(block)"))
fun NoiseSettings.surfaceRules(block: MaterialRulesScope.() -> Unit) = materialRules(block)

@Deprecated("Renamed to materialRules, like the game's field since 26.3.", ReplaceWith("materialRules(*rules)"))
fun NoiseSettings.surfaceRules(vararg rules: MaterialRule) = materialRules(*rules)

/**
 * Appends a sequence of [MaterialRule] objects based on the provided block.
 */
fun MaterialRulesScope.sequence(block: MaterialRulesScope.() -> Unit) = apply { rules += Sequence(buildMaterialRules(block)) }

/**
 * Appends a new sequence of MaterialRule objects.
 */
fun MaterialRulesScope.sequence(vararg sequenceRules: MaterialRule) = apply { rules += Sequence(sequenceRules.toList()) }
