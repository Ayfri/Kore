package io.github.ayfri.kore.features.worldgen.densityfunction

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.features.worldgen.densityfunction.types.DensityFunctionType
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument

/**
 * Builder scope for declaring density functions via [densityFunctions].
 *
 * Each [io.github.ayfri.kore.features.worldgen.densityfunction.types.DensityFunctionType] (e.g.
 * [io.github.ayfri.kore.features.worldgen.densityfunction.types.Abs]) exposes an extension function on this class
 * that creates one [DensityFunction] file, so a density function can only ever hold the single node type it was
 * declared with.
 */
data class DensityFunctionsScope(val dp: DataPack) {
	/** Writes [type] to the file [fileName] and returns the argument pointing at it. */
	internal fun register(fileName: String, type: DensityFunctionType): DensityFunctionArgument {
		val densityFunction = DensityFunction(fileName, type)
		dp.densityFunctions += densityFunction
		return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
	}
}
