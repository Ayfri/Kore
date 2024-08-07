package io.github.ayfri.kore.features.enchantment.providers.types

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = EnchantmentProviderType.Companion.EnchantmentProviderTypeSerializer::class)
sealed class EnchantmentProviderType {
	companion object {
		data object EnchantmentProviderTypeSerializer : NamespacedPolymorphicSerializer<EnchantmentProviderType>(EnchantmentProviderType::class)
	}
}
