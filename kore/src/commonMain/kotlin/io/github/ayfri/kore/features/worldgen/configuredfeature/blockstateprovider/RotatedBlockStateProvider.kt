package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.BlockStateBuilder
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import kotlinx.serialization.Serializable

/**
 * Places the block state of [state] rotated towards [direction], or a random one when it is omitted. It rotates the
 * `axis` property of a block (logs, pillars) and the `facing` property of a block (dispensers, pistons).
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rotated
 *
 * @property direction The direction the block faces, random at every position when `null`.
 * @property state The provider of the block state placed at every position, its `axis` or `facing` property being
 * rotated.
 */
@Serializable
data class RotatedBlockStateProvider(
	var direction: Direction? = null,
	var state: BlockStateProvider = SimpleBlockStateProvider(),
) : BlockStateProvider()

/**
 * Creates a `rotated` block state provider, rotating the block state of [state] towards [direction] (random when omitted).
 *
 * ```kotlin
 * tree("oak") {
 *     trunkProvider = rotatedBlockProvider(simpleStateProvider(Blocks.OAK_LOG))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rotated
 */
fun BlockStateProviderScope.rotatedBlockProvider(state: BlockStateProvider, direction: Direction? = null) =
	RotatedBlockStateProvider(direction, state)

/**
 * Creates a `rotated` block state provider placing [state] rotated towards [direction] (random when omitted).
 *
 * ```kotlin
 * rotatedBlockProvider(blockState(Blocks.OAK_LOG), Direction.UP)
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rotated
 */
fun BlockStateProviderScope.rotatedBlockProvider(state: BlockState = blockStateStone(), direction: Direction? = null) =
	RotatedBlockStateProvider(direction, SimpleBlockStateProvider(state))

/**
 * Creates a `rotated` block state provider placing [id] with [properties], rotated towards [direction] (random when
 * omitted).
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rotated
 */
fun BlockStateProviderScope.rotatedBlockProvider(
	id: BlockArgument,
	properties: Map<String, String>,
	direction: Direction? = null,
) = RotatedBlockStateProvider(direction, SimpleBlockStateProvider(blockState(id, properties)))

/**
 * Creates a `rotated` block state provider placing [id] rotated towards [direction] (random when omitted), its other block
 * state properties being set in [block].
 *
 * ```kotlin
 * rotatedBlockProvider(Blocks.OAK_LOG)
 * rotatedBlockProvider(Blocks.DISPENSER, Direction.NORTH)
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rotated
 */
fun BlockStateProviderScope.rotatedBlockProvider(
	id: BlockArgument,
	direction: Direction? = null,
	block: BlockStateBuilder.() -> Unit = {},
) = RotatedBlockStateProvider(direction, SimpleBlockStateProvider(blockState(id, block)))
