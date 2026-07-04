package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.effects.EnchantmentEffect
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = EntityEffect.Companion.EntityEffectSerializer::class)
sealed class EntityEffect : EnchantmentEffect {
	var requirements: List<PredicateCondition>? = null

	companion object {
		@OptIn(InternalSerializationApi::class)
		data object EntityEffectSerializer :
			NamespacedPolymorphicSerializer<EntityEffect>(entityEffectSealedSerializer())
	}
}

fun EntityEffect.requirements(block: Predicate.() -> Unit = {}) {
	requirements = Predicate().apply(block).predicateConditions
}
