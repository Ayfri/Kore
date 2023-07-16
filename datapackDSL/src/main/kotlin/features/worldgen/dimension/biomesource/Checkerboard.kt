package features.worldgen.dimension.biomesource

import arguments.Argument
import kotlinx.serialization.Serializable
import serializers.InlinableList

@Serializable
data class Checkerboard(
	var biomes: InlinableList<Argument.Biome>,
	var scale: Int? = null,
) : BiomeSource()

fun checkerboard(scale: Int? = null, biomes: List<Argument.Biome>) = Checkerboard(biomes, scale)

fun checkerboard(scale: Int? = null, vararg biomes: Argument.Biome) = Checkerboard(biomes.toList(), scale)

fun checkerboard(scale: Int? = null, block: MutableList<Argument.Biome>.() -> Unit) = Checkerboard(buildList(block), scale)
