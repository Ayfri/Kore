package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BlockStateProvider.Companion.BlockStateProviderSerializer::class)
sealed class BlockStateProvider {
	companion object {
		data object BlockStateProviderSerializer : NamespacedPolymorphicSerializer<BlockStateProvider>(BlockStateProvider::class)
	}
}
