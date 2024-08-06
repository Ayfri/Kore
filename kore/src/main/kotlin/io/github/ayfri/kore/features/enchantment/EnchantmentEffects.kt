package io.github.ayfri.kore.features.enchantment

import io.github.ayfri.kore.features.enchantment.effects.builders.EffectBuilder
import io.github.ayfri.kore.generated.EnchantmentEffectComponents
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

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
		data object EnchantmentEffectsSerializer : InlineSerializer<EnchantmentEffects, Map<String, EffectBuilder>>(
			kSerializer = MapSerializer(String.serializer(), EffectBuilder.serializer()),
			property = EnchantmentEffects::effects
		)
	}
}
