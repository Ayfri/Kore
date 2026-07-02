package io.github.ayfri.kore.features.enchantments

import io.github.ayfri.kore.features.enchantments.effects.builders.EffectBuilder
import io.github.ayfri.kore.generated.EnchantmentEffectComponents
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable(with = EnchantmentEffects.Companion.EnchantmentEffectsSerializer::class)
data class EnchantmentEffects(var effects: Map<String, EffectBuilder> = emptyMap()) {
	operator fun get(key: String) = effects[key]

	operator fun set(key: String, value: EffectBuilder) {
		effects = effects + (key to value)
	}

	operator fun set(key: EnchantmentEffectComponents, value: EffectBuilder) {
		effects = effects + (key.asId() to value)
	}

	operator fun contains(key: String) = key in effects
	operator fun contains(key: EnchantmentEffectComponents) = key.asId() in effects

	companion object {
		data object EnchantmentEffectsSerializer : InlineAutoSerializer<EnchantmentEffects, Map<String, EffectBuilder>>(
			serializer<Map<String, EffectBuilder>>(),
			EnchantmentEffects::effects,
			::EnchantmentEffects
		)
	}
}
