package io.github.ayfri.kore.features.enchantment.effects.builders

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = EffectBuilder.Companion.EffectBuilderSerializer::class)
sealed class EffectBuilder {
	companion object {
		data object EffectBuilderSerializer : NamespacedPolymorphicSerializer<EffectBuilder>(EffectBuilder::class, skipOutputName = true)
	}
}
