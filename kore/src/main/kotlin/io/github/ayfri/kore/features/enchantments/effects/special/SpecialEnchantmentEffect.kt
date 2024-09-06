package io.github.ayfri.kore.features.enchantments.effects.special

import io.github.ayfri.kore.features.enchantments.effects.EnchantmentEffect
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SpecialEnchantmentEffect.Companion.SpecialEnchantmentEffectSerializer::class)
sealed class SpecialEnchantmentEffect : EnchantmentEffect {
	companion object {
		data object SpecialEnchantmentEffectSerializer : NamespacedPolymorphicSerializer<SpecialEnchantmentEffect>(SpecialEnchantmentEffect::class)
	}
}
