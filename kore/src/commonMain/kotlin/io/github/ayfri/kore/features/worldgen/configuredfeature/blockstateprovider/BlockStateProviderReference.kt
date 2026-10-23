package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.generated.arguments.worldgen.types.BlockStateProviderArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * A block state provider declared in its own file or built into the game, written as its ID. The registry has no tags.
 *
 * ```kotlin
 * tree("oak") {
 *     belowTrunkProvider = reference(BlockStateProviders.SOIL_BENEATH_TREE)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider
 *
 * @property id The ID of the block state provider file.
 */
@Serializable(with = BlockStateProviderReference.Companion.BlockStateProviderReferenceSerializer::class)
data class BlockStateProviderReference(var id: BlockStateProviderArgument) : BlockStateProvider() {
	companion object {
		data object BlockStateProviderReferenceSerializer :
			InlineAutoSerializer<BlockStateProviderReference, BlockStateProviderArgument>(
				serializer<BlockStateProviderArgument>(),
				BlockStateProviderReference::id,
				::BlockStateProviderReference,
				"BlockStateProviderReference",
			)
	}
}

/**
 * Creates a reference to the block state provider [id], written as its bare ID.
 *
 * ```kotlin
 * tree("oak") {
 *     belowTrunkProvider = reference(BlockStateProviders.SOIL_BENEATH_TREE)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider
 */
fun BlockStateProviderScope.reference(id: BlockStateProviderArgument) = BlockStateProviderReference(id)
