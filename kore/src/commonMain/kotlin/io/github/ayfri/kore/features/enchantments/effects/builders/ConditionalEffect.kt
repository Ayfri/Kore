package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.EnchantmentEffect
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.reference
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * One entry of an effect component, pairing an effect with the conditions it runs under.
 *
 * Built by the effect builders, which lift the requirements set on the effect itself into [requirements].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#Effect_components
 *
 * @property effect The effect run by the entry.
 * @property requirements The conditions the entry runs under, always running when `null`.
 */
@Serializable
data class ConditionalEffect(
	var effect: EnchantmentEffect,
	var requirements: InlinableList<PredicateCondition>? = null,
)

/** Sets [ConditionalEffect.requirements] to the predicate conditions built in [block]. */
fun ConditionalEffect.requirements(block: Predicate.() -> Unit) =
	apply { requirements = Predicate().apply(block).predicateConditions }

/** Sets [ConditionalEffect.requirements] to references to the already registered [conditions]. */
fun ConditionalEffect.requirements(vararg conditions: PredicateArgument) = apply {
	requirements = Predicate().apply { conditions.forEach { reference(it) } }.predicateConditions
}
