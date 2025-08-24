package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.special.EmptyConditionalEffect
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = EmptyConditionalEffectBuilder.Companion.EmptyConditionalEffectBuilderSerializer::class)
data class EmptyConditionalEffectBuilder(var effects: List<EmptyConditionalEffect> = emptyList()) : EffectBuilder() {
	companion object {
		data object EmptyConditionalEffectBuilderSerializer : InlineAutoSerializer<EmptyConditionalEffectBuilder>(
			EmptyConditionalEffectBuilder::class
		)
	}
}

fun EmptyConditionalEffectBuilder.sound(block: EmptyConditionalEffect.() -> Unit = {}) =
	apply { effects += EmptyConditionalEffect().apply(block) }
