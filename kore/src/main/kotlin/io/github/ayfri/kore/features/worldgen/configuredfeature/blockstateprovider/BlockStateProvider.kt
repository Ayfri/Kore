package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = BlockStateProvider.Companion.BlockStateProviderSerializer::class)
sealed class BlockStateProvider {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object BlockStateProviderSerializer :
			NamespacedPolymorphicSerializer<BlockStateProvider>(blockStateProviderSealedSerializer())
	}
}
