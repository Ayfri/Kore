package features.worldgen.densityfunction

import DataPack
import Generator
import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.types.DensityFunctionType
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
	densityFunctions += DensityFunction(fileName, type).apply(block)
	return DensityFunctionArgument(fileName, name)
}
