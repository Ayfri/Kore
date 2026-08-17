package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

/**
 * Represents an entry of a [NoiseGradient] surface rule.
 *
 * @property state The block state to place, or `null` to place no block at this position in the gradient.
 */
@Serializable
data class NoiseGradientEntry(
	var state: BlockState? = null,
)

/**
 * Represents a surface rule that picks a block state from a gradient of entries, indexed by the value of a noise.
 *
 * @property gradient The list of entries in the gradient.
 * @property noise The noise used to index into the gradient.
 */
@Serializable
data class NoiseGradient(
	var gradient: List<NoiseGradientEntry>,
	var noise: NoiseArgument,
) : SurfaceRule()

/**
 * Builder scope for the entries of a [NoiseGradient] surface rule.
 *
 * @property entries The entries appended so far, in gradient order.
 */
class NoiseGradientScope {
	val entries = mutableListOf<NoiseGradientEntry>()
}

/**
 * Appends a noise gradient surface rule.
 */
fun SurfaceRulesScope.noiseGradient(noise: NoiseArgument, gradient: List<NoiseGradientEntry>) =
	apply { rules += NoiseGradient(gradient, noise) }

/**
 * Appends a noise gradient surface rule.
 */
fun SurfaceRulesScope.noiseGradient(noise: NoiseArgument, block: NoiseGradientScope.() -> Unit) =
	apply { rules += NoiseGradient(NoiseGradientScope().apply(block).entries, noise) }

/**
 * Appends an entry placing [name] with the given block-state properties.
 */
fun NoiseGradientScope.state(name: BlockArgument, block: MutableMap<String, String>.() -> Unit = {}) =
	apply { entries += NoiseGradientEntry(BlockState(name, buildMap(block))) }

/**
 * Appends an entry placing [name] with the given block-state [properties].
 */
fun NoiseGradientScope.state(name: BlockArgument, properties: Map<String, String>) =
	apply { entries += NoiseGradientEntry(BlockState(name, properties)) }

/**
 * Appends an entry leaving the position untouched.
 */
fun NoiseGradientScope.empty() = apply { entries += NoiseGradientEntry() }
