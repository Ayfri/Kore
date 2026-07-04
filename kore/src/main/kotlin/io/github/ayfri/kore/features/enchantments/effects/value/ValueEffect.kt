package io.github.ayfri.kore.features.enchantments.effects.value

import io.github.ayfri.kore.features.enchantments.effects.EnchantmentEffect
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = ValueEffect.Companion.ValueEffectSerializer::class)
sealed class ValueEffect : EnchantmentEffect {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object ValueEffectSerializer : NamespacedPolymorphicSerializer<ValueEffect>(valueEffectSealedSerializer())
	}
}
