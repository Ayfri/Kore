package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BlockEntityModifier.Companion.BlockEntityModifierSerializer::class)
sealed class BlockEntityModifier {
	companion object {
		data object BlockEntityModifierSerializer : NamespacedPolymorphicSerializer<BlockEntityModifier>(BlockEntityModifier::class)
	}
}
