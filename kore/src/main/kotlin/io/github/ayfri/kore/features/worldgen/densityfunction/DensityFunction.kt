package io.github.ayfri.kore.features.worldgen.densityfunction

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.densityfunction.types.DensityFunctionType
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

data class DensityFunction(
	@Transient
	override var fileName: String = "density_function",
	var type: DensityFunctionType,
) : Generator("worldgen/density_function") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(type)
}

fun DataPack.densityFunction(
	fileName: String,
	type: DensityFunctionType,
	block: DensityFunction.() -> Unit = {},
): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, type).apply(block)
	densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: name)
}
