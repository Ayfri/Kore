package io.github.ayfri.kore.features.blocktransformers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.generated.arguments.types.BlockTransformerArgument
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/** Where the [BlockTransformerRule.loot] drops when a block is transformed. */
@Serializable(with = BlockTransformDropStrategy.Companion.BlockTransformDropStrategySerializer::class)
enum class BlockTransformDropStrategy {
	CLICKED_FACE,
	FROM_MIDDLE;

	companion object {
		data object BlockTransformDropStrategySerializer : LowercaseSerializer<BlockTransformDropStrategy>(entries)
	}
}

/** The particles spawned when a block is transformed. */
@Serializable(with = BlockTransformParticle.Companion.BlockTransformParticleSerializer::class)
enum class BlockTransformParticle {
	NONE,
	SCRAPE,
	WAX_OFF,
	WAX_ON;

	companion object {
		data object BlockTransformParticleSerializer : LowercaseSerializer<BlockTransformParticle>(entries)
	}
}

/** How many blocks a transformation changes. */
@Serializable(with = BlockTransformType.Companion.BlockTransformTypeSerializer::class)
enum class BlockTransformType {
	COPPER_CHEST,
	SINGLE_BLOCK;

	companion object {
		data object BlockTransformTypeSerializer : LowercaseSerializer<BlockTransformType>(entries)
	}
}

/**
 * A rule of a [BlockTransformerFile]: [blockStateProvider] gives the block state the clicked block turns into.
 *
 * The next rule of the file is tried when [blockStateProvider] gives nothing, or when the clicked face is in
 * [disallowedFaces].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#block_transformer
 *
 * @property blockStateProvider The provider of the new block state, usually a rule-based provider checking the clicked block.
 * @property consumeOnUse Whether the item is consumed on use, only for stackable items. Defaults to `true`.
 * @property disallowedFaces The clicked faces the rule ignores. Defaults to none, so every face is allowed.
 * @property dropStrategy Where the [loot] drops, from the clicked face or from the middle of the block. Defaults to `from_middle`.
 * @property itemDamagePerUse The durability lost per use, at least `0`, only for unstackable items. Defaults to `0`.
 * @property loot The loot table dropped on success. Defaults to no drop.
 * @property particle The particles spawned on success. Defaults to `none`.
 * @property sound The sound played on success. Defaults to no sound.
 * @property transformType Whether a single block is transformed, or both halves of a double copper chest with
 * `copper_chest`. Defaults to `single_block`.
 * @property updateFromNeighbors Whether the transformed block updates its shape from its neighbors. Defaults to `true`.
 */
@Serializable
data class BlockTransformerRule(
	var blockStateProvider: BlockStateProvider,
	var consumeOnUse: Boolean? = null,
	var disallowedFaces: List<Direction>? = null,
	var dropStrategy: BlockTransformDropStrategy? = null,
	var itemDamagePerUse: Int? = null,
	var loot: LootTableArgument? = null,
	var particle: BlockTransformParticle? = null,
	var sound: SoundEventArgument? = null,
	var transformType: BlockTransformType? = null,
	var updateFromNeighbors: Boolean? = null,
)

/**
 * Data-driven block transformer, the list of [rules] a `block_transformer` item component points to, like axes stripping
 * logs or honeycombs waxing copper. A file holds 1 to 200 rules, tried in order until one gives a block state, and is
 * written as the list of rules directly.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/block-transformers
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#block_transformer
 */
data class BlockTransformerFile(
	@Transient
	override var fileName: String = "block_transformer",
	var rules: List<BlockTransformerRule> = mutableListOf(),
) : Generator("block_transformer"), BlockStateProviderScope {
	override fun generateJson(dataPack: DataPack): String {
		require(rules.size in 1..MAX_RULES) { "Block transformer '$fileName' needs 1 to $MAX_RULES rules, got ${rules.size}." }
		return dataPack.jsonEncoder.encodeToString(rules)
	}

	companion object {
		const val MAX_RULES = 200
	}
}

/**
 * Creates a block transformer file with the rules added in [block], and returns its ID for the `blockTransformer` item
 * component.
 *
 * ```kotlin
 * val strip = blockTransformer("strip_oak") {
 *     rule(simpleStateProvider(Blocks.STRIPPED_OAK_LOG)) {
 *         sound = SoundEvents.Item.Axe.STRIP
 *     }
 * }
 *
 * Items.IRON_AXE { blockTransformer(strip) }
 * ```
 *
 * Produces `data/<namespace>/block_transformer/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/block-transformers
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#block_transformer
 */
fun DataPack.blockTransformer(
	fileName: String = "block_transformer",
	block: BlockTransformerFile.() -> Unit,
): BlockTransformerArgument {
	val file = BlockTransformerFile(fileName).apply(block)
	blockTransformers += file
	return BlockTransformerArgument(fileName, file.namespace ?: name)
}

/**
 * Adds a rule turning the clicked block into the state given by [blockStateProvider], its options being set in [block].
 *
 * ```kotlin
 * rule(simpleStateProvider(Blocks.DIRT_PATH)) {
 *     disallowedFaces = listOf(Direction.DOWN)
 *     sound = SoundEvents.Item.Shovel.FLATTEN
 * }
 * ```
 */
fun BlockTransformerFile.rule(blockStateProvider: BlockStateProvider, block: BlockTransformerRule.() -> Unit = {}) = apply {
	rules += BlockTransformerRule(blockStateProvider).apply(block)
}
