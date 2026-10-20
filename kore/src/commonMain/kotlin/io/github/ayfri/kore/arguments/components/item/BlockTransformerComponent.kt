package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.DataPackShapedSerializer
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

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
 * A rule of the [BlockTransformerComponent]: [blockStateProvider] gives the block state the clicked block turns into.
 *
 * The next rule of the component is tried when [blockStateProvider] gives nothing, or when the clicked face is in
 * [disallowedFaces].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#block_transformer
 *
 * @property blockStateProvider The provider of the new block state, usually a rule-based provider checking the clicked block.
 * @property consumeOnUse Whether the item is consumed on use, only for stackable items. Defaults to `true`.
 * @property disallowedFaces The clicked faces the rule ignores. Defaults to none, so every face is allowed.
 * @property dropStrategy Where the [loot] drops, from the clicked face or from the middle of the block. Defaults to `from_middle`.
 * @property itemDamagePerUse The durability lost per use, only for unstackable items. Defaults to `1`.
 * @property loot The loot table dropped on success. Defaults to no drop.
 * @property particle The particles spawned on success. Defaults to `none`.
 * @property sound The sound played on success. Defaults to no sound.
 * @property transformType Whether a single block is transformed, or both halves of a double copper chest with
 * `copper_chest`. Defaults to `single_block`.
 * @property updateFromNeighbors Whether the transformed block updates its shape from its neighbors. Defaults to `true`.
 */
@Serializable
data class BlockTransformerRule(
	@SerialName("block_state_provider")
	@Serializable(with = BlockStateProviderComponentSerializer::class)
	var blockStateProvider: BlockStateProvider,
	@SerialName("consume_on_use") var consumeOnUse: Boolean? = null,
	@SerialName("disallowed_faces") var disallowedFaces: List<Direction>? = null,
	@SerialName("drop_strategy") var dropStrategy: BlockTransformDropStrategy? = null,
	@SerialName("item_damage_per_use") var itemDamagePerUse: Int? = null,
	var loot: LootTableArgument? = null,
	var particle: BlockTransformParticle? = null,
	var sound: SoundEventArgument? = null,
	@SerialName("transform_type") var transformType: BlockTransformType? = null,
	@SerialName("update_from_neighbors") var updateFromNeighbors: Boolean? = null,
) {
	companion object {
		data object BlockStateProviderComponentSerializer :
			DataPackShapedSerializer<BlockStateProvider>(BlockStateProvider.serializer())
	}
}

/**
 * Represents the `minecraft:block_transformer` item component, which turns the block the item is used on into another
 * one, like axes stripping logs or honeycombs waxing copper.
 *
 * Takes 1 to 200 rules, tried in order until one gives a block state. Serializes as the list of rules directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#block_transformer
 */
@Serializable(with = BlockTransformerComponent.Companion.BlockTransformerComponentSerializer::class)
data class BlockTransformerComponent(var rules: List<BlockTransformerRule>) : Component(), BlockStateProviderScope {
	companion object {
		data object BlockTransformerComponentSerializer :
			InlineAutoSerializer<BlockTransformerComponent, List<BlockTransformerRule>>(
				serializer<List<BlockTransformerRule>>(),
				BlockTransformerComponent::rules,
				::BlockTransformerComponent
			)
	}
}

/**
 * Turns the block this item is used on into another one, the rules being added in [block].
 *
 * ```kotlin
 * blockTransformer {
 *     rule(ruleBasedStateProvider {
 *         rule(copyPropertiesProvider(simpleStateProvider(Blocks.STRIPPED_OAK_LOG, mapOf("axis" to "y")))) {
 *             matchingBlocks(Blocks.OAK_LOG)
 *         }
 *     }) {
 *         sound = SoundEvents.Item.Axe.STRIP
 *     }
 * }
 * ```
 */
fun ComponentsScope.blockTransformer(block: BlockTransformerComponent.() -> Unit) = apply {
	this[ItemComponentTypes.BLOCK_TRANSFORMER] = BlockTransformerComponent(mutableListOf()).apply(block)
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
fun BlockTransformerComponent.rule(blockStateProvider: BlockStateProvider, block: BlockTransformerRule.() -> Unit = {}) = apply {
	rules += BlockTransformerRule(blockStateProvider).apply(block)
}
