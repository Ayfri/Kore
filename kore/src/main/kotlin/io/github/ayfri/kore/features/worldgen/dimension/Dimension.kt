package io.github.ayfri.kore.features.worldgen.dimension

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.dimension.generator.Debug
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import io.github.ayfri.kore.generated.arguments.types.DimensionTypeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import io.github.ayfri.kore.features.worldgen.dimension.generator.Generator as DimensionGenerator

/**
 * Data-driven dimension definition.
 *
 * A dimension ties a dimension type (rules like skylight, respawn, height) to a world generator
 * (noise/flat/debug). It represents one world (Overworld, Nether, End or a custom one).
 *
 * JSON format reference: https://minecraft.wiki/w/Dimension_definition
 */
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
 * Creates a dimension using a builder block.
 *
 * Choose the generator with helpers like [debugGenerator], [noiseGenerator] or [flatGenerator].
 *
 * Produces `data/<namespace>/dimension/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/Custom_dimension
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
