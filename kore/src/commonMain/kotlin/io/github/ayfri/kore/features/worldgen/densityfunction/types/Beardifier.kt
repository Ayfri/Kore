package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Adds beards for structures (e.g. villages, mineshafts) so nearby terrain blends with them. Takes no
 * parameters; entirely managed by the game.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data object Beardifier : DensityFunctionType()

/**
 * Adds a `beardifier` density function to the data pack, blending nearby terrain into structures
 * (e.g. villages, mineshafts). Takes no parameters; entirely managed by the game.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.beardifier(fileName: String): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, Beardifier)
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}
