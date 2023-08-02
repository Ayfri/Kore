package features.worldgen.noisesettings.rules

import features.worldgen.noisesettings.NoiseSettings
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
 * Creates a sequence of [SurfaceRule] objects based on the provided block.
 */
fun sequence(block: MutableList<SurfaceRule>.() -> Unit) = Sequence(buildList(block))

/**
 * Creates a new sequence of SurfaceRule objects.
 */
fun sequence(vararg rules: SurfaceRule) = Sequence(rules.toList())
