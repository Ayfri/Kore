package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.BlockTransformerArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:block_transformer` item component, which turns the block the item is used on into another
 * one, like axes stripping logs or honeycombs waxing copper.
 *
 * Points to a `block_transformer` file by ID, serialized as the bare ID (inlined). Inline rule lists aren't possible
 * because the game only reads this component from a registry entry.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#block_transformer
 */
@Serializable(with = BlockTransformerComponent.Companion.BlockTransformerComponentSerializer::class)
data class BlockTransformerComponent(var blockTransformer: BlockTransformerArgument) : Component() {
	companion object {
		data object BlockTransformerComponentSerializer :
			InlineAutoSerializer<BlockTransformerComponent, BlockTransformerArgument>(
				serializer<BlockTransformerArgument>(),
				BlockTransformerComponent::blockTransformer,
				::BlockTransformerComponent
			)
	}
}

/**
 * Turns the block this item is used on into another one, following the rules of the `block_transformer` file
 * [blockTransformer], created with `blockTransformer("name") { ... }` on the data pack.
 *
 * ```kotlin
 * val strip = blockTransformer("strip_oak") { rule(simpleStateProvider(Blocks.STRIPPED_OAK_LOG)) }
 * Items.IRON_AXE { blockTransformer(strip) }  // minecraft:iron_axe[block_transformer="my_pack:strip_oak"]
 * ```
 */
fun ComponentsScope.blockTransformer(blockTransformer: BlockTransformerArgument) = apply {
	this[ItemComponentTypes.BLOCK_TRANSFORMER] = BlockTransformerComponent(blockTransformer)
}
