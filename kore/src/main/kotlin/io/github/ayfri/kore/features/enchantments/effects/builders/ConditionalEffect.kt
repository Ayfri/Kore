package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.EnchantmentEffect
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class ConditionalEffect(
	var effect: EnchantmentEffect,
	var requirements: InlinableList<PredicateCondition>? = null,
)

fun ConditionalEffect.requirements(block: Predicate.() -> Unit) = apply { requirements = Predicate().apply(block).predicateConditions }
