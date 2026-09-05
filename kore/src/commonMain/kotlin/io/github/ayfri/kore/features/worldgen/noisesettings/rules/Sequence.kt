package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.features.worldgen.noisesettings.NoiseSettings
import kotlinx.serialization.Serializable

/**
 * Represents a sequence of surface rules.
 *
 * @property sequence The list of surface rules in the sequence.
 */
@Serializable
data class Sequence(
	var sequence: List<SurfaceRule>,
) : SurfaceRule()

/**
 * Sets the surface rules for the noise settings.
 */
fun NoiseSettings.surfaceRules(block: SurfaceRulesScope.() -> Unit) {
	surfaceRule = Sequence(buildSurfaceRules(block))
}

/**
 * Sets the surface rules for the noise settings.
 */
fun NoiseSettings.surfaceRules(vararg rules: SurfaceRule) {
	surfaceRule = Sequence(rules.toList())
}

/**
 * Appends a sequence of [SurfaceRule] objects based on the provided block.
 */
fun SurfaceRulesScope.sequence(block: SurfaceRulesScope.() -> Unit) = apply { rules += Sequence(buildSurfaceRules(block)) }

/**
 * Appends a new sequence of SurfaceRule objects.
 */
fun SurfaceRulesScope.sequence(vararg sequenceRules: SurfaceRule) = apply { rules += Sequence(sequenceRules.toList()) }
