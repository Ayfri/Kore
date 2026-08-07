package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import kotlinx.serialization.Serializable

@Serializable
data class Block(
	var resultState: BlockState? = null,
) : SurfaceRule()

/**
 * Appends a block surface rule.
 */
fun MutableList<SurfaceRule>.block(name: BlockArgument, block: MutableMap<String, String>.() -> Unit = {}) =
	apply { add(Block(BlockState(name, buildMap(block)))) }

/**
 * Appends a block surface rule.
 */
fun MutableList<SurfaceRule>.block(name: BlockArgument, properties: Map<String, String>) =
	apply { add(Block(BlockState(name, properties))) }
