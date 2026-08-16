package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Samples the legacy blended noise used before the 1.18 terrain rewrite, scaled horizontally by
 * [xzScale]/[xzFactor] and vertically by [yScale]/[yFactor], with smearing controlled by
 * [smearScaleMultiplier].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class OldBlendedNoise(
	var xzScale: Double = 0.0,
	var yScale: Double = 0.0,
	var xzFactor: Double = 0.0,
	var yFactor: Double = 0.0,
	var smearScaleMultiplier: Double = 0.0,
) : DensityFunctionType()

/**
 * Adds an `old_blended_noise` density function to the data pack, sampling the legacy blended noise used
 * before the 1.18 terrain rewrite.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.oldBlendedNoise(
	fileName: String,
	xzScale: Double = 0.0,
	yScale: Double = 0.0,
	xzFactor: Double = 0.0,
	yFactor: Double = 0.0,
	smearScaleMultiplier: Double = 0.0,
): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, OldBlendedNoise(xzScale, yScale, xzFactor, yFactor, smearScaleMultiplier))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/**
 * Adds an `old_blended_noise` density function to the data pack, sampling the legacy blended noise used
 * before the 1.18 terrain rewrite. Configure it in [block].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.oldBlendedNoise(fileName: String, block: OldBlendedNoise.() -> Unit): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, OldBlendedNoise().apply(block))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}
