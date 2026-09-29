package io.github.ayfri.kore.features.enchantments.providers.types

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * How an [io.github.ayfri.kore.features.enchantments.providers.EnchantmentProvider] picks the enchantments it hands
 * out and the level of each of them.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_provider
 */
@GeneratedSealedSerializer
@Serializable(with = EnchantmentProviderType.Companion.EnchantmentProviderTypeSerializer::class)
sealed class EnchantmentProviderType {
	companion object {
		data object EnchantmentProviderTypeSerializer :
			NamespacedPolymorphicSerializer<EnchantmentProviderType>(enchantmentProviderTypeSealedSerializer())
	}
}
