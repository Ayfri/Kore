package io.github.ayfri.kore.features.enchantment.effects

import io.github.ayfri.kore.features.enchantment.effects.entity.EntityEffect
import io.github.ayfri.kore.features.enchantment.effects.special.SpecialEnchantmentEffect
import io.github.ayfri.kore.features.enchantment.effects.value.ValueEffect
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = EnchantmentEffect.Companion.EnchantmentEffectSerializer::class)
interface EnchantmentEffect {
	companion object {
		data object EnchantmentEffectSerializer : KSerializer<EnchantmentEffect> {
			override val descriptor = buildClassSerialDescriptor("EnchantmentEffect")

			override fun deserialize(decoder: Decoder) = error("EnchantmentEffect cannot be deserialized")

			override fun serialize(encoder: Encoder, value: EnchantmentEffect) = when (value) {
				is AttributeEffect -> encoder.encodeSerializableValue(AttributeEffect.serializer(), value)
				is EntityEffect -> encoder.encodeSerializableValue(EntityEffect.serializer(), value)
				is SpecialEnchantmentEffect -> encoder.encodeSerializableValue(SpecialEnchantmentEffect.serializer(), value)
				is ValueEffect -> encoder.encodeSerializableValue(ValueEffect.serializer(), value)
				else -> error("Unknown effect type: $value")
			}
		}
	}
}
