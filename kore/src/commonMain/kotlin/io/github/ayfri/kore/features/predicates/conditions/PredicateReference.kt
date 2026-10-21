package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.generated.arguments.PredicateOrTagArgument
import io.github.ayfri.kore.generated.arguments.tagged.PredicateTagArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.serializers.IdOrTagReferenceSerializer
import io.github.ayfri.kore.serializers.splitNamespacedId
import kotlinx.serialization.Serializable

/**
 * Passes when the predicate [id] points to passes, written as the bare string `"my_pack:my_predicate"` wherever a
 * condition is expected. A tag (`"#my_pack:my_tag"`) is only valid in a list position: `all_of` and `any_of` terms.
 * A cyclic reference makes the pack fail to load, and a predicate file can't be a reference alone.
 * A list can't mix references and inline values, the game rejects it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable(with = PredicateReference.Companion.PredicateReferenceSerializer::class)
data class PredicateReference(var id: PredicateOrTagArgument) : PredicateCondition() {
	companion object {
		data object PredicateReferenceSerializer : IdOrTagReferenceSerializer<PredicateReference, PredicateOrTagArgument>(
			PredicateReference::id,
			::PredicateReference,
			{ name, namespace -> PredicateArgument(name, namespace) },
			{ name, namespace -> PredicateTagArgument(name, namespace) },
			"predicate_reference",
		)
	}
}

/**
 * Adds a [PredicateReference] condition pointing at [predicate].
 *
 * ```
 * reference(PredicateArgument("is_night", "my_pack"))  // "my_pack:is_night"
 * ```
 */
fun Predicate.reference(predicate: PredicateArgument) {
	predicateConditions += PredicateReference(predicate)
}

/** Adds a [PredicateReference] condition pointing at the predicate located at [name], e.g. `my_pack:my_predicate`. */
fun Predicate.reference(name: String) {
	val (path, namespace) = name.splitNamespacedId()
	predicateConditions += PredicateReference(PredicateArgument(path, namespace))
}

/** Adds a [PredicateReference] condition pointing at [predicate], resolved against the data pack it belongs to. */
context(dp: DataPack)
fun Predicate.reference(predicate: Predicate) {
	predicateConditions += PredicateReference(PredicateArgument(predicate.fileName, predicate.namespace ?: dp.name))
}

/**
 * Adds a [PredicateReference] condition pointing at the predicate tag [tag], written `"#my_pack:my_tag"`. The game only
 * accepts a tag in a list position, so use it inside [allOf] or [anyOf].
 *
 * ```
 * anyOf { reference(PredicateTagArgument("night_like", "my_pack")) }  // "terms": "#my_pack:night_like"
 * ```
 */
fun Predicate.reference(tag: PredicateTagArgument) {
	predicateConditions += PredicateReference(tag)
}
