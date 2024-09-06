package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.special.EmptyConditionalEffect
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable(with = EmptyConditionalEffectBuilder.Companion.EmptyConditionalEffectBuilderSerializer::class)
data class EmptyConditionalEffectBuilder(
	var effects: List<EmptyConditionalEffect> = emptyList(),
) : EffectBuilder() {
	companion object {
		data object EmptyConditionalEffectBuilderSerializer : InlineSerializer<EmptyConditionalEffectBuilder, List<EmptyConditionalEffect>>(
			kSerializer = ListSerializer(EmptyConditionalEffect.serializer()),
			property = EmptyConditionalEffectBuilder::effects
		)
	}
}

fun EmptyConditionalEffectBuilder.sound(block: EmptyConditionalEffect.() -> Unit = {}) =
	apply { effects += EmptyConditionalEffect().apply(block) }
