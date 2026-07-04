package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = EffectBuilder.Companion.EffectBuilderSerializer::class)
sealed class EffectBuilder {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object EffectBuilderSerializer :
			NamespacedPolymorphicSerializer<EffectBuilder>(effectBuilderSealedSerializer(), skipOutputName = true)
	}
}
