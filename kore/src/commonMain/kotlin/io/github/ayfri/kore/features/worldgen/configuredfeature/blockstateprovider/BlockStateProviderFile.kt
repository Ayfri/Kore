package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.worldgen.types.BlockStateProviderArgument
import kotlinx.serialization.Transient

/**
 * Data-driven block state provider, usable by ID wherever the game takes a block state provider, such as the
 * configured features, the tree decorators, the root placers and the `replace_block` and `replace_disk` enchantment
 * effects.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features#block-state-provider-files
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider
 */
data class BlockStateProviderFile(
	@Transient
	override var fileName: String = "block_state_provider",
	var provider: BlockStateProvider,
) : Generator("worldgen/block_state_provider") {
	override fun generateJson(dataPack: DataPack): String {
		require(provider !is BlockStateProviderReference) { "Block state provider file '$fileName' must hold an inline provider, not a reference." }
		return dataPack.jsonEncoder.encodeToString(BlockStateProvider.serializer(), provider)
	}
}

/**
 * Creates a block state provider file holding the provider returned by [block].
 *
 * ```kotlin
 * val dirtOrGrass = blockStateProvider("dirt_or_grass") {
 *     weightedStateProvider {
 *         entry(Blocks.DIRT, weight = 3)
 *         entry(Blocks.GRASS_BLOCK)
 *     }
 * }
 *
 * simpleBlock("patch") {
 *     toPlace = reference(dirtOrGrass)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/block_state_provider/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features#block-state-provider-files
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider
 */
fun DataPack.blockStateProvider(
	fileName: String = "block_state_provider",
	block: BlockStateProviderScope.() -> BlockStateProvider,
): BlockStateProviderArgument {
	val file = BlockStateProviderFile(fileName, object : BlockStateProviderScope {}.block())
	blockStateProviders += file
	return BlockStateProviderArgument(fileName, file.namespace ?: name)
}
