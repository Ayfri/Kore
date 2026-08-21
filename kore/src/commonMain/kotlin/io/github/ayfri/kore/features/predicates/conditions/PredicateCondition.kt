package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * One check of a [Predicate][io.github.ayfri.kore.features.predicates.Predicate], serialized as
 * `{ "condition": "<type>", ... }`.
 *
 * Every builder is an extension on `Predicate` (e.g. `randomChance`, `entityProperties`), so they only resolve
 * inside a `predicate("...") { }` block, and each one appends its condition to the predicate.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@GeneratedSealedSerializer
@Serializable(with = PredicateCondition.Companion.PredicateConditionSerializer::class)
sealed class PredicateCondition {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object PredicateConditionSerializer : NamespacedPolymorphicSerializer<PredicateCondition>(
			predicateConditionSealedSerializer(),
			outputName = "condition"
		)
	}
}
