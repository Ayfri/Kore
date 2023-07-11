package features.worldgen.biome.types

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class MoodSound(
	var sound: Argument.Sound? = null,
	var tickDelay: Int = 6000,
	var blockSearchExtent: Int = 8,
	var offset: Float = 2f,
)
