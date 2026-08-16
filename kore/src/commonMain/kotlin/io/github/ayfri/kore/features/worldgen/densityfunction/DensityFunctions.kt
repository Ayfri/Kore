package io.github.ayfri.kore.features.worldgen.densityfunction

import io.github.ayfri.kore.DataPack

/**
 * Builder scope for declaring density functions via [densityFunctions].
 *
 * Each [io.github.ayfri.kore.features.worldgen.densityfunction.types.DensityFunctionType] (e.g.
 * [io.github.ayfri.kore.features.worldgen.densityfunction.types.Abs]) exposes an extension function on this class
 * that creates one [DensityFunction] file, so a density function can only ever hold the single node type it was
 * declared with.
 */
data class DensityFunctionsScope(val dp: DataPack)
