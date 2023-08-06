package features.worldgen.configuredcarver

import data.block.BlockState
import kotlinx.serialization.Serializable

@Serializable
data class DebugSettings(
	var debugMode: Boolean? = null,
	var airState: BlockState? = null,
	var waterState: BlockState? = null,
	var lavaState: BlockState? = null,
	var barrierState: BlockState? = null,
)
