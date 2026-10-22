package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import kotlinx.serialization.Serializable

/**
 * Represents a material rule that places a fixed block state.
 *
 * @property resultState The block state to place, or `null` to place no block.
 */
@Serializable
data class Block(
	var resultState: BlockState? = null,
) : MaterialRule()

/**
 * Appends a block material rule placing [id], its block state properties being set in [block].
 */
fun MaterialRulesScope.block(id: BlockArgument, block: MutableMap<String, String>.() -> Unit = {}) =
	apply { rules += Block(BlockState(id, buildMap(block))) }

/**
 * Appends a block material rule placing [id] with [properties].
 */
fun MaterialRulesScope.block(id: BlockArgument, properties: Map<String, String>) =
	apply { rules += Block(BlockState(id, properties)) }
