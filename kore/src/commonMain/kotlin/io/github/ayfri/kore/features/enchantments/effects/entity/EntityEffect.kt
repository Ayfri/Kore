package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.effects.EnchantmentEffect
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Something an enchantment does to an entity or to the world, such as igniting a target, replacing a block or
 * running a function.
 *
 * [requirements] is not part of the effect itself: the builder that appends the effect lifts it next to it, in the
 * `requirements` field of the surrounding conditional effect.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#Entity_effects
 */
@GeneratedSealedSerializer
@Serializable(with = EntityEffect.Companion.EntityEffectSerializer::class)
sealed class EntityEffect : EnchantmentEffect {
	/** The conditions the effect runs under, always running when `null`. Only honored at the top level of a component. */
	var requirements: List<PredicateCondition>? = null

	companion object {
		@OptIn(InternalSerializationApi::class)
		data object EntityEffectSerializer :
			NamespacedPolymorphicSerializer<EntityEffect>(entityEffectSealedSerializer())
	}
}

/** Sets [EntityEffect.requirements] to the predicate conditions built in [block]. */
fun EntityEffect.requirements(block: Predicate.() -> Unit = {}) {
	requirements = Predicate().apply(block).predicateConditions
}
