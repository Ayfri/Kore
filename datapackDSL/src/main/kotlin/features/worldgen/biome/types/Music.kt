package features.worldgen.biome.types

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable

data class Music(
	var sound: Argument.Sound,
	var minDelay: Int = 0,
	var maxDelay: Int = 0,
	var replaceCurrentMusic: Boolean = false,
)
