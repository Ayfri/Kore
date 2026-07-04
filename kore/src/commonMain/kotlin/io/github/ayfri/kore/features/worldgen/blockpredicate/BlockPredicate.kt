package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = BlockPredicate.Companion.BlockProviderSerializer::class)
sealed class BlockPredicate {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object BlockProviderSerializer :
			NamespacedPolymorphicSerializer<BlockPredicate>(blockPredicateSealedSerializer())
	}
}
