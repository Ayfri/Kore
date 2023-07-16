package features.worldgen.dimension.biomesource

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class Fixed(var biome: Argument.Biome) : BiomeSource()

fun fixed(biome: Argument.Biome) = Fixed(biome)
