package io.github.ayfri.kore.features.worldgen.noisesettings.rules

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
 * Appends a noise gradient surface rule.
 */
fun MutableList<SurfaceRule>.noiseGradient(noise: NoiseArgument, gradient: List<NoiseGradientEntry>) =
	apply { add(NoiseGradient(gradient, noise)) }

/**
 * Appends a noise gradient surface rule.
 */
fun MutableList<SurfaceRule>.noiseGradient(noise: NoiseArgument, block: MutableList<NoiseGradientEntry>.() -> Unit) =
	apply { add(NoiseGradient(buildList(block), noise)) }

fun MutableList<NoiseGradientEntry>.entry(state: BlockState? = null) = apply {
	add(NoiseGradientEntry(state))
}
