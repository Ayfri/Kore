package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = BlockEntityModifier.Companion.BlockEntityModifierSerializer::class)
sealed class BlockEntityModifier {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object BlockEntityModifierSerializer :
			NamespacedPolymorphicSerializer<BlockEntityModifier>(blockEntityModifierSealedSerializer())
	}
}
