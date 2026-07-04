package io.github.ayfri.kore.arguments.components.consumable

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = ConsumeEffect.Companion.ConsumeEffectSerializer::class)
sealed class ConsumeEffect {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object ConsumeEffectSerializer :
			NamespacedPolymorphicSerializer<ConsumeEffect>(consumeEffectSealedSerializer(), skipOutputName = true)
	}
}
