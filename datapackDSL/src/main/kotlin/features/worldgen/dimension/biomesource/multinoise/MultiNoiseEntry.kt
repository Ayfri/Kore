package features.worldgen.dimension.biomesource.multinoise

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class MultiNoiseEntry(
	var biome: Argument.Biome,
	var parameters: MultiNoiseBiomeSourceParameters
)

/**
 * Creates a [MultiNoiseEntry] with all values set to `0.0` by default for [MultiNoiseBiomeSourceParameters].
 */
fun multiNoiseEntry(
	biome: Argument.Biome,
	block: MultiNoiseBiomeSourceParameters.() -> Unit = {}
) = MultiNoiseEntry(biome, multiNoiseBiomeSourceParameters(block))
