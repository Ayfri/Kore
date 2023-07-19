package features.worldgen.dimension.biomesource

import arguments.types.resources.worldgen.BiomeArgument
import kotlinx.serialization.Serializable

@Serializable
data class Fixed(var biome: BiomeArgument) : BiomeSource()

fun fixed(biome: BiomeArgument) = Fixed(biome)
