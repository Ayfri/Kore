package io.github.ayfri.kore.features.enchantments.effects.special

import io.github.ayfri.kore.features.enchantments.effects.EnchantmentEffect
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = SpecialEnchantmentEffect.Companion.SpecialEnchantmentEffectSerializer::class)
sealed class SpecialEnchantmentEffect : EnchantmentEffect {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object SpecialEnchantmentEffectSerializer :
			NamespacedPolymorphicSerializer<SpecialEnchantmentEffect>(specialEnchantmentEffectSealedSerializer())
	}
}
