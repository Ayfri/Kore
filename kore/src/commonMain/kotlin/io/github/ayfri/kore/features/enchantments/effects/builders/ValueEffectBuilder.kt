package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.value.ValueEffect
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * The list of conditional value effects of a component such as `damage`, `ammo_use` or `mob_experience`.
 *
 * Serialized as the bare list, each entry pairing an effect with the requirements it applies under.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#Value_effects
 */
@Serializable(with = ValueEffectBuilder.Companion.ValueEffectBuilderSerializer::class)
data class ValueEffectBuilder(var effects: List<ConditionalEffect> = emptyList()) : EffectBuilder(), ValueEffectScope {
	override fun addEffect(effect: ValueEffect) {
		effects += ConditionalEffect(effect, effect.requirements)
	}

	companion object {
		data object ValueEffectBuilderSerializer : InlineAutoSerializer<ValueEffectBuilder, List<ConditionalEffect>>(
			serializer<List<ConditionalEffect>>(),
			ValueEffectBuilder::effects,
			::ValueEffectBuilder,
			serialName = "ValueEffectBuilder",
		)
	}
}
