package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places the default state of a random block among [blocks].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#random_block_provider
 *
 * @property blocks A block, a block tag or a list of blocks, one of them being picked at every position.
 */
@Serializable
data class RandomBlockProvider(
	var blocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : BlockStateProvider()

/**
 * Creates a `random_block_provider`, placing the default state of a random block among [blocks] (blocks or block tags).
 *
 * ```kotlin
 * simpleBlock("flower") {
 *     toPlace = randomBlockProvider(Blocks.DANDELION, Blocks.POPPY)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#random_block_provider
 */
fun BlockStateProviderScope.randomBlockProvider(vararg blocks: BlockOrTagArgument) = RandomBlockProvider(blocks.toList())
