package io.github.ayfri.kore.features.worldgen.densityfunction

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.densityfunction.types.DensityFunctionType
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

/**
 * Data-driven density function node.
 *
 * A density function is a composable expression graph used by the noise router to shape terrain
 * (e.g. adding ridges, caves, erosion). This serializes the chosen function node.
 *
 * JSON format reference: https://minecraft.wiki/w/Density_function
 */
data class DensityFunction(
	@Transient
	override var fileName: String = "density_function",
	var type: DensityFunctionType,
) : Generator("worldgen/density_function") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(type)
}

/**
 * Creates a density function file wiring a specific function node.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/Density_function
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 */
fun DataPack.densityFunction(
	fileName: String,
	type: DensityFunctionType,
	block: DensityFunction.() -> Unit = {},
): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, type).apply(block)
	densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: name)
}
