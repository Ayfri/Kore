package io.github.ayfri.kore.features.enchantments.effects.special

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class EmptyConditionalEffect(
	val effect: Map<String, String> = emptyMap(),
	var requirements: InlinableList<PredicateCondition>? = null,
) : SpecialEnchantmentEffect()

fun EmptyConditionalEffect.requirements(block: Predicate.() -> Unit = {}) {
	requirements = Predicate().apply(block).predicateConditions
}
