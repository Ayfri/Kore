package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.entity.EntityEffect
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * The list of conditional entity effects of a component such as `hit_block`, `tick` or `location_changed`.
 *
 * Serialized as the bare list, each entry pairing an effect with the requirements it runs under.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#Entity_effects
 */
@Serializable(with = EntityEffectBuilder.Companion.EntityEffectBuilderSerializer::class)
data class EntityEffectBuilder(var effects: List<ConditionalEffect> = emptyList()) : EffectBuilder(), EntityEffectScope {
	override fun addEffect(effect: EntityEffect) {
		effects += ConditionalEffect(effect, effect.requirements)
	}

	companion object {
		data object EntityEffectBuilderSerializer : InlineAutoSerializer<EntityEffectBuilder, List<ConditionalEffect>>(
			serializer<List<ConditionalEffect>>(),
			EntityEffectBuilder::effects,
			::EntityEffectBuilder,
			serialName = "EntityEffectBuilder",
		)
	}
}
