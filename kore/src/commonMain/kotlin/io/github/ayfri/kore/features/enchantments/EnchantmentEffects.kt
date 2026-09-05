package io.github.ayfri.kore.features.enchantments

import io.github.ayfri.kore.features.enchantments.effects.builders.EffectBuilder
import io.github.ayfri.kore.generated.EnchantmentEffectComponents
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * The `effects` object of an [Enchantment], mapping each effect component id to what it does.
 *
 * Components are usually set through the builders of `EffectsComponents.kt`, such as `damage { }` or
 * `postAttack { }`, the operators here being the escape hatch for a component Kore does not name yet.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#Effect_components
 *
 * @property effects The component of each effect id, such as `minecraft:damage`.
 */
@Serializable(with = EnchantmentEffects.Companion.EnchantmentEffectsSerializer::class)
data class EnchantmentEffects(var effects: Map<String, EffectBuilder> = emptyMap()) {
	/** Returns the component set for [key], or `null` when the enchantment does not have it. */
	operator fun get(key: String) = effects[key]

	/** Returns the component set for [key], or `null` when the enchantment does not have it. */
	operator fun get(key: EnchantmentEffectComponents) = effects[key.asId()]

	/** Sets the component of the raw effect id [key], overwriting any previous one. */
	operator fun set(key: String, value: EffectBuilder) {
		effects = effects + (key to value)
	}

	/** Sets the component of [key], overwriting any previous one. */
	operator fun set(key: EnchantmentEffectComponents, value: EffectBuilder) {
		effects = effects + (key.asId() to value)
	}

	/** Returns whether the enchantment sets the raw effect id [key]. */
	operator fun contains(key: String) = key in effects

	/** Returns whether the enchantment sets [key]. */
	operator fun contains(key: EnchantmentEffectComponents) = key.asId() in effects

	companion object {
		data object EnchantmentEffectsSerializer : InlineAutoSerializer<EnchantmentEffects, Map<String, EffectBuilder>>(
			serializer<Map<String, EffectBuilder>>(),
			EnchantmentEffects::effects,
			::EnchantmentEffects
		)
	}
}
