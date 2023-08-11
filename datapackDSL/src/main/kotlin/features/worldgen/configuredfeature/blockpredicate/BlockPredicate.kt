package features.worldgen.configuredfeature.blockpredicate

import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BlockPredicate.Companion.BlockProviderSerializer::class)
sealed class BlockPredicate {
	companion object {
		data object BlockProviderSerializer : NamespacedPolymorphicSerializer<BlockPredicate>(BlockPredicate::class)
	}
}
