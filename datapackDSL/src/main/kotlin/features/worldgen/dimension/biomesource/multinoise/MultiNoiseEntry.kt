package features.worldgen.dimension.biomesource.multinoise

import arguments.types.resources.BiomeArgument
import kotlinx.serialization.Serializable

@Serializable
data class MultiNoiseEntry(
	var biome: BiomeArgument,
	var parameters: MultiNoiseBiomeSourceParameters
)

/**
 * Creates a [MultiNoiseEntry] with all values set to `0.0` by default for [MultiNoiseBiomeSourceParameters].
 */
fun multiNoiseEntry(
	biome: BiomeArgument,
	block: MultiNoiseBiomeSourceParameters.() -> Unit = {}
) = MultiNoiseEntry(biome, multiNoiseBiomeSourceParameters(block))
