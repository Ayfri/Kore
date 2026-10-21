package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * One check of a [Predicate][io.github.ayfri.kore.features.predicates.Predicate], serialized as
 * `{ "type": "<type>", ... }`, or as the bare string of its ID for a [PredicateReference].
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
		data object PredicateConditionSerializer :
			NamespacedPolymorphicSerializer<PredicateCondition>(predicateConditionSealedSerializer())
	}
}

/** Returns the one condition declared by [block], failing with a message naming [builder] otherwise. */
internal fun singleCondition(builder: String, block: Predicate.() -> Unit): PredicateCondition {
	val conditions = Predicate().apply(block).predicateConditions
	require(conditions.size == 1) { "'$builder' takes exactly one condition, got ${conditions.size}." }
	return conditions.first()
}
