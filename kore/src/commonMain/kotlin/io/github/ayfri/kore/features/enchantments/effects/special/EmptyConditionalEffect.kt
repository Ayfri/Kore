package io.github.ayfri.kore.features.enchantments.effects.special

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * An entry of a component whose payload is empty, such as `minecraft:damage_immunity`, where only [requirements]
 * carries information.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#damage_immunity
 *
 * @property effect The always empty payload of the entry.
 * @property requirements The conditions the entry applies under, always applying when `null`.
 */
@Serializable
data class EmptyConditionalEffect(
	val effect: Map<String, String> = emptyMap(),
	var requirements: InlinableList<PredicateCondition>? = null,
) : SpecialEnchantmentEffect()
