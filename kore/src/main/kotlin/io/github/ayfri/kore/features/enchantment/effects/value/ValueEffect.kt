package io.github.ayfri.kore.features.enchantment.effects.value

import io.github.ayfri.kore.features.enchantment.effects.EnchantmentEffect
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ValueEffect.Companion.ValueEffectSerializer::class)
sealed class ValueEffect : EnchantmentEffect {
	companion object {
		data object ValueEffectSerializer : NamespacedPolymorphicSerializer<ValueEffect>(ValueEffect::class)
	}
}
