package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BlockPredicate.Companion.BlockProviderSerializer::class)
sealed class BlockPredicate {
	companion object {
		data object BlockProviderSerializer : NamespacedPolymorphicSerializer<BlockPredicate>(BlockPredicate::class)
	}
}
