package io.github.ayfri.kore.features.worldgen.configuredcarver

import io.github.ayfri.kore.data.block.BlockState
import kotlinx.serialization.Serializable

@Serializable
data class DebugSettings(
	var debugMode: Boolean? = null,
	var airState: BlockState? = null,
	var waterState: BlockState? = null,
	var lavaState: BlockState? = null,
	var barrierState: BlockState? = null,
)
