package io.github.ayfri.kore.features.enchantments.effects.special

import io.github.ayfri.kore.features.enchantments.effects.EnchantmentEffect
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * An enchantment effect that neither computes a number nor acts on an entity, such as the crossbow charging sounds
 * or the empty payload of `minecraft:damage_immunity`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#Effect_components
 */
@GeneratedSealedSerializer
@Serializable(with = SpecialEnchantmentEffect.Companion.SpecialEnchantmentEffectSerializer::class)
sealed class SpecialEnchantmentEffect : EnchantmentEffect {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object SpecialEnchantmentEffectSerializer :
			NamespacedPolymorphicSerializer<SpecialEnchantmentEffect>(specialEnchantmentEffectSealedSerializer())
	}
}
