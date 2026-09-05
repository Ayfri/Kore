package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.BlockStateBuilder
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.data.block.blockStateStone
import kotlinx.serialization.Serializable

/**
 * Places [state] with its `axis` property randomly rotated, used by the vanilla trees to vary their wood logs.
 *
 * The `axis` property of [state] is ignored, a random one is picked at every position.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rotated_block_provider
 *
 * @property state The block state placed at every position, its `axis` property being randomized.
 */
@Serializable
data class RotatedBlockProvider(
	var state: BlockState = blockStateStone(),
) : BlockStateProvider()

/**
 * Creates a `rotated_block_provider`, placing [state] with a random `axis`.
 *
 * ```kotlin
 * tree("oak") {
 *     trunkProvider = rotatedBlockProvider(blockState(Blocks.OAK_LOG))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rotated_block_provider
 */
fun BlockStateProviderScope.rotatedBlockProvider(state: BlockState = blockStateStone()) = RotatedBlockProvider(state)

/**
 * Creates a `rotated_block_provider` placing [name] with [properties] and a random `axis`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rotated_block_provider
 */
fun BlockStateProviderScope.rotatedBlockProvider(name: BlockArgument, properties: Map<String, String>) =
	RotatedBlockProvider(blockState(name, properties))

/**
 * Creates a `rotated_block_provider` placing [name] with a random `axis`, its other block state properties being set
 * in [block].
 *
 * ```kotlin
 * rotatedBlockProvider(Blocks.OAK_LOG)
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rotated_block_provider
 */
fun BlockStateProviderScope.rotatedBlockProvider(name: BlockArgument, block: BlockStateBuilder.() -> Unit = {}) =
	RotatedBlockProvider(blockState(name, block))
