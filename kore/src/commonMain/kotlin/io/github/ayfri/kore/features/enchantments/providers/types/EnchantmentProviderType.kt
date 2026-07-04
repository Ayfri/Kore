package io.github.ayfri.kore.features.enchantments.providers.types

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = EnchantmentProviderType.Companion.EnchantmentProviderTypeSerializer::class)
sealed class EnchantmentProviderType {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object EnchantmentProviderTypeSerializer :
			NamespacedPolymorphicSerializer<EnchantmentProviderType>(enchantmentProviderTypeSealedSerializer())
	}
}
