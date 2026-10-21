package io.github.ayfri.kore.data.block

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * A block (or fluid) state as the game stores it in JSON and NBT: the bare block ID for the default state, an object with
 * `id` and `properties` otherwise. Both forms are read back.
 *
 * ```
 * blockState(Blocks.STONE)                        // "minecraft:stone"
 * blockState(Blocks.OAK_STAIRS, "facing" to "north") // {"id":"minecraft:oak_stairs","properties":{"facing":"north"}}
 * ```
 */
@KeepGeneratedSerializer
@Serializable(with = BlockState.Companion.BlockStateSerializer::class)
data class BlockState(
	var id: BlockArgument,
	var properties: Map<String, String>? = null,
) {
	init {
		if (properties?.isEmpty() == true) properties = null

		val states = id.states
		if (states.isNotEmpty()) {
			properties = states.toMap()
			id.states.clear()
		}
	}

	companion object {
		data object BlockStateSerializer : SinglePropertySimplifierSerializer<BlockState>(generatedSerializer(), "id")
	}
}
