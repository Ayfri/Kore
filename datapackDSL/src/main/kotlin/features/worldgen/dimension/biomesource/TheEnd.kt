package features.worldgen.dimension.biomesource

import kotlinx.serialization.Serializable

@Serializable
data object TheEnd : BiomeSource()

fun theEnd() = TheEnd
