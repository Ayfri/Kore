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
fun NoiseSettings.surfaceRules(block: MutableList<SurfaceRule>.() -> Unit) {
	surfaceRule = Sequence(buildList(block))
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
fun MutableList<SurfaceRule>.sequence(block: MutableList<SurfaceRule>.() -> Unit) = apply { add(Sequence(buildList(block))) }

/**
 * Appends a new sequence of SurfaceRule objects.
 */
fun MutableList<SurfaceRule>.sequence(vararg rules: SurfaceRule) = apply { add(Sequence(rules.toList())) }
