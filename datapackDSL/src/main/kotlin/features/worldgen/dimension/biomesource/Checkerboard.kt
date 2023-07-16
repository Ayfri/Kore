package features.worldgen.dimension.biomesource

import arguments.types.resources.BiomeArgument
import kotlinx.serialization.Serializable
import serializers.InlinableList

@Serializable
data class Checkerboard(
	var biomes: InlinableList<BiomeArgument>,
	var scale: Int? = null,
) : BiomeSource()

fun checkerboard(scale: Int? = null, biomes: List<BiomeArgument>) = Checkerboard(biomes, scale)

fun checkerboard(scale: Int? = null, vararg biomes: BiomeArgument) = Checkerboard(biomes.toList(), scale)

fun checkerboard(scale: Int? = null, block: MutableList<BiomeArgument>.() -> Unit) = Checkerboard(buildList(block), scale)
