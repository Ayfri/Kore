package io.github.ayfri.kore.features.worldgen.dimension.biomesource

import io.github.ayfri.kore.arguments.types.resources.worldgen.BiomeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Checkerboard(
	var biomes: InlinableList<BiomeArgument>,
	var scale: Int? = null,
) : BiomeSource()

fun checkerboard(scale: Int? = null, biomes: List<BiomeArgument>) = Checkerboard(biomes, scale)

fun checkerboard(scale: Int? = null, vararg biomes: BiomeArgument) = Checkerboard(biomes.toList(), scale)

fun checkerboard(scale: Int? = null, block: MutableList<BiomeArgument>.() -> Unit) = Checkerboard(buildList(block), scale)
