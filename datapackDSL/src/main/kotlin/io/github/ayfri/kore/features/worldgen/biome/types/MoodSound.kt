package io.github.ayfri.kore.features.worldgen.biome.types

import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import kotlinx.serialization.Serializable

@Serializable
data class MoodSound(
	var sound: SoundArgument? = null,
	var tickDelay: Int = 6000,
	var blockSearchExtent: Int = 8,
	var offset: Float = 2f,
)
