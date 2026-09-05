package io.github.ayfri.kore.features.enchantments.effects

import io.github.ayfri.kore.features.enchantments.effects.entity.EntityEffect
import io.github.ayfri.kore.features.enchantments.effects.special.SpecialEnchantmentEffect
import io.github.ayfri.kore.features.enchantments.effects.value.ValueEffect
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Anything an effect component can hold: an [AttributeEffect], an [EntityEffect], a [ValueEffect] or a
 * [SpecialEnchantmentEffect].
 *
 * The four families have unrelated serializers, so this one only dispatches to the right one. Enchantments are
 * written, never read back, hence the deserialization error.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#Effect_components
 */
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
