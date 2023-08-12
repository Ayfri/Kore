package features.worldgen.processorlist.types.rule.blockentitymodifier

import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BlockEntityModifier.Companion.BlockEntityModifierSerializer::class)
sealed class BlockEntityModifier {
	companion object {
		data object BlockEntityModifierSerializer : NamespacedPolymorphicSerializer<BlockEntityModifier>(BlockEntityModifier::class)
	}
}
