package io.github.ayfri.kore.features.worldgen.densityfunction

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.densityfunction.types.DensityFunctionType
import kotlinx.serialization.Transient

/**
 * Data-driven density function node.
 *
 * A density function is a composable expression graph used by the noise router to shape terrain
 * (e.g. adding ridges, caves, erosion). This serializes the chosen function node.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
data class DensityFunction(
	@Transient
	override var fileName: String = "density_function",
	var type: DensityFunctionType,
) : Generator("worldgen/density_function") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(type)
}

val DataPack.densityFunctionsBuilder get() = DensityFunctionsScope(this)

/**
 * Declares density functions using Kore's DSL builder, one call per node type (e.g. [io.github.ayfri.kore.features.worldgen.densityfunction.types.abs]).
 *
 * Produces one `data/<namespace>/worldgen/density_function/<fileName>.json` per call inside [block].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DataPack.densityFunctions(block: DensityFunctionsScope.() -> Unit) = densityFunctionsBuilder.apply(block)
