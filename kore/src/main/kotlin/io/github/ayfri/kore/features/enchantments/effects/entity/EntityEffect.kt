package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.effects.EnchantmentEffect
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = EntityEffect.Companion.EntityEffectSerializer::class)
sealed class EntityEffect : EnchantmentEffect {
	var requirements: List<PredicateCondition>? = null

	companion object {
		data object EntityEffectSerializer : NamespacedPolymorphicSerializer<EntityEffect>(EntityEffect::class)
	}
}

fun EntityEffect.requirements(block: Predicate.() -> Unit = {}) {
	requirements = Predicate().apply(block).predicateConditions
}
