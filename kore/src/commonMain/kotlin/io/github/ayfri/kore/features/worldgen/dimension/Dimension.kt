package io.github.ayfri.kore.features.worldgen.dimension

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.dimension.generator.Debug
import io.github.ayfri.kore.features.worldgen.dimension.generator.debugGenerator
import io.github.ayfri.kore.features.worldgen.dimension.generator.flatGenerator
import io.github.ayfri.kore.features.worldgen.dimension.generator.noiseGenerator
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import io.github.ayfri.kore.generated.arguments.types.DimensionTypeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import io.github.ayfri.kore.features.worldgen.dimension.generator.Generator as DimensionGenerator

/**
 * One world of the game, pairing the rules of a dimension type with the generator building its terrain.
 *
 * The same class is written as its own file by [dimension] and inlined into the `dimensions` map of a world preset.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/dimensions
 * Minecraft Wiki: https://minecraft.wiki/w/Dimension_definition
 *
 * @property type The dimension type holding the height bounds, lighting and environment attributes of the world.
 * @property generator How the terrain is built, the debug world until one of the generator builders is called.
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
 * Creates a dimension of the given [type], configured in [block].
 *
 * Pick the generator with [debugGenerator], [flatGenerator] or [noiseGenerator], all scoped to [block]; without one
 * the dimension generates the debug world.
 *
 * ```kotlin
 * dimension("skylands", DimensionTypes.OVERWORLD) {
 *     noiseGenerator(NoiseSettings.OVERWORLD, multiNoise(BiomePresets.OVERWORLD))
 * }
 * ```
 *
 * Produces `data/<namespace>/dimension/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/dimensions
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_dimension
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
