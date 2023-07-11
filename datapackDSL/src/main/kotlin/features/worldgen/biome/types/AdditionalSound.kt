package features.worldgen.biome.types

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class AdditionalSound(
	var sound: Argument.Sound,
	var tickChance: Float
)
