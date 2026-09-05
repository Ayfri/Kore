package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.special.EmptyConditionalEffect
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * The entries of a component whose payload is empty, such as `damage_immunity`, where only the requirements of each
 * entry carry information.
 *
 * Serialized as the bare list.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#damage_immunity
 */
@Serializable(with = EmptyConditionalEffectBuilder.Companion.EmptyConditionalEffectBuilderSerializer::class)
data class EmptyConditionalEffectBuilder(var effects: List<EmptyConditionalEffect> = emptyList()) : EffectBuilder() {
	companion object {
		data object EmptyConditionalEffectBuilderSerializer :
			InlineAutoSerializer<EmptyConditionalEffectBuilder, List<EmptyConditionalEffect>>(
				serializer<List<EmptyConditionalEffect>>(),
				EmptyConditionalEffectBuilder::effects,
				::EmptyConditionalEffectBuilder,
				serialName = "EmptyConditionalEffectBuilder",
			)
	}
}

/**
 * Appends an entry applying under the conditions built in [block].
 *
 * ```kotlin
 * damageImmunity {
 *     requirements {
 *         damageSourcePropertiesCondition {
 *             tags(Tags.DamageType.IS_FIRE to true)
 *         }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#damage_immunity
 */
fun EmptyConditionalEffectBuilder.requirements(block: Predicate.() -> Unit = {}) =
	apply { effects += EmptyConditionalEffect(requirements = Predicate().apply(block).predicateConditions) }

/** Appends an entry applying unconditionally. */
fun EmptyConditionalEffectBuilder.always() = apply { effects += EmptyConditionalEffect() }
