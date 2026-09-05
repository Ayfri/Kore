package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Picks the block state placed at a given position, used by most of the configured features, by the tree decorators
 * and root placers, and by the `replace_block` / `replace_disk` enchantment effects.
 *
 * Every builder is an extension on [BlockStateProviderScope], so they only resolve inside a block that actually
 * accepts a block state provider, such as `simpleBlock("...") { }` or `weightedStateProvider { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider
 */
@GeneratedSealedSerializer
@Serializable(with = BlockStateProvider.Companion.BlockStateProviderSerializer::class)
sealed class BlockStateProvider {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object BlockStateProviderSerializer :
			NamespacedPolymorphicSerializer<BlockStateProvider>(blockStateProviderSealedSerializer())
	}
}
