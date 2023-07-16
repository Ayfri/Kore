package features.worldgen.biome.types

import arguments.types.resources.SoundArgument
import kotlinx.serialization.Serializable

@Serializable
data class AdditionalSound(
	var sound: SoundArgument,
	var tickChance: Float
)
