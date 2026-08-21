package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import kotlinx.serialization.Serializable

/**
 * Passes when the predicate [name] points to passes. A cyclic reference makes the pack fail to load.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - reference](https://minecraft.wiki/w/Predicate#reference)
 */
@Serializable
data class Reference(
	var name: PredicateArgument,
) : PredicateCondition()

/** Adds a [Reference] condition pointing at [predicate]. */
fun Predicate.reference(predicate: PredicateArgument) {
	predicateConditions += Reference(predicate)
}

/** Adds a [Reference] condition pointing at the predicate located at [name], e.g. `my_pack:my_predicate`. */
fun Predicate.reference(name: String) {
	val (namespace, path) = name.split(':', limit = 2).let { if (it.size == 2) it[0] to it[1] else "minecraft" to it[0] }
	predicateConditions += Reference(PredicateArgument(path, namespace))
}

/** Adds a [Reference] condition pointing at [predicate], resolved against the data pack it belongs to. */
context(dp: DataPack)
fun Predicate.reference(predicate: Predicate) {
	predicateConditions += Reference(PredicateArgument(predicate.fileName, predicate.namespace ?: dp.name))
}
