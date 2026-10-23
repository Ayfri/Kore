package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.BlockStateBuilder
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.data.block.blockStateStone
import kotlinx.serialization.Serializable

/**
 * Always places [state], the simplest and most common block state provider.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#simple
 *
 * @property state The block state placed at every position.
 */
@Serializable
data class SimpleBlockStateProvider(
	var state: BlockState = blockStateStone(),
) : BlockStateProvider()

/**
 * Creates a `simple` block state provider, always placing [state].
 *
 * ```kotlin
 * simpleBlock("dandelion") {
 *     toPlace = simpleStateProvider(blockState(Blocks.DANDELION))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#simple
 */
fun BlockStateProviderScope.simpleStateProvider(state: BlockState = blockStateStone()) = SimpleBlockStateProvider(state)

/**
 * Creates a `simple` block state provider placing [id] with [properties].
 *
 * ```kotlin
 * simpleStateProvider(Blocks.OAK_LOG, mapOf("axis" to "y"))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#simple
 */
fun BlockStateProviderScope.simpleStateProvider(id: BlockArgument, properties: Map<String, String>) =
	SimpleBlockStateProvider(blockState(id, properties))

/**
 * Creates a `simple` block state provider placing [id], its block state properties being set in [block].
 *
 * ```kotlin
 * simpleStateProvider(Blocks.OAK_LOG) {
 *     properties { this["axis"] = "y" }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#simple
 */
fun BlockStateProviderScope.simpleStateProvider(id: BlockArgument, block: BlockStateBuilder.() -> Unit = {}) =
	SimpleBlockStateProvider(blockState(id, block))
