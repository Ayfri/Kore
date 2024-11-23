package io.github.ayfri.kore.arguments.components.consumable

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ConsumeEffect.Companion.ConsumeEffectSerializer::class)
sealed class ConsumeEffect {
	companion object {
		data object ConsumeEffectSerializer : NamespacedPolymorphicSerializer<ConsumeEffect>(ConsumeEffect::class, skipOutputName = true)
	}
}
