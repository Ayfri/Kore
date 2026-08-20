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
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#simple_state_provider
 *
 * @property state The block state placed at every position.
 */
@Serializable
data class SimpleStateProvider(
	var state: BlockState = blockStateStone(),
) : BlockStateProvider()

/**
 * Creates a `simple_state_provider`, always placing [state].
 *
 * ```kotlin
 * simpleBlock("dandelion") {
 *     toPlace = simpleStateProvider(blockState(Blocks.DANDELION))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#simple_state_provider
 */
fun BlockStateProviderScope.simpleStateProvider(state: BlockState = blockStateStone()) = SimpleStateProvider(state)

/**
 * Creates a `simple_state_provider` placing [name] with [properties].
 *
 * ```kotlin
 * simpleStateProvider(Blocks.OAK_LOG, mapOf("axis" to "y"))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#simple_state_provider
 */
fun BlockStateProviderScope.simpleStateProvider(name: BlockArgument, properties: Map<String, String>) =
	SimpleStateProvider(blockState(name, properties))

/**
 * Creates a `simple_state_provider` placing [name], its block state properties being set in [block].
 *
 * ```kotlin
 * simpleStateProvider(Blocks.OAK_LOG) {
 *     properties { this["axis"] = "y" }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#simple_state_provider
 */
fun BlockStateProviderScope.simpleStateProvider(name: BlockArgument, block: BlockStateBuilder.() -> Unit = {}) =
	SimpleStateProvider(blockState(name, block))
