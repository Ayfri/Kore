package io.github.ayfri.kore.features.worldgen.dimension.generator

import io.github.ayfri.kore.features.worldgen.dimension.Dimension
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import kotlinx.serialization.Serializable

@Serializable
data class Flat(
	var settings: FlatGeneratorSettings
) : Generator()

fun Dimension.flatGenerator(biome: BiomeArgument, block: FlatGeneratorSettings.() -> Unit) =
	Flat(FlatGeneratorSettings(biome).apply(block)).also { generator = it }
