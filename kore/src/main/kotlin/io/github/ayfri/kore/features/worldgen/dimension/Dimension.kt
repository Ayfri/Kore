package io.github.ayfri.kore.features.worldgen.dimension

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.dimension.generator.Debug
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import io.github.ayfri.kore.generated.arguments.types.DimensionTypeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import io.github.ayfri.kore.features.worldgen.dimension.generator.Generator as DimensionGenerator

@Serializable
data class Dimension(
	@Transient
	override var fileName: String = "dimension",
	var type: DimensionTypeArgument,
	var generator: DimensionGenerator = Debug,
) : Generator("dimension") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a new Dimension with [Debug] world generator.
 * See [features.worldgen.dimension.generator.Noise] for using noise (overworld) generator.
 */
fun DataPack.dimension(
	fileName: String,
	type: DimensionTypeArgument,
	block: Dimension.() -> Unit = {},
): DimensionArgument {
	val dimension = Dimension(fileName, type).apply(block)
	dimensions += dimension
	return DimensionArgument(fileName, dimension.namespace ?: name)
}
