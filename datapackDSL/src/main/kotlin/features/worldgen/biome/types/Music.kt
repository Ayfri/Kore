package features.worldgen.biome.types

import arguments.types.resources.SoundArgument
import kotlinx.serialization.Serializable

@Serializable

data class Music(
	var sound: SoundArgument,
	var minDelay: Int = 0,
	var maxDelay: Int = 0,
	var replaceCurrentMusic: Boolean = false,
)
