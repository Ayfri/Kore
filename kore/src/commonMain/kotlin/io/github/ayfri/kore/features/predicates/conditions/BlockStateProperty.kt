package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class BlockStateProperty(
	var block: BlockArgument,
	var properties: Map<String, String>? = null,
) : PredicateCondition()

fun Predicate.blockStateProperty(block: BlockArgument, properties: Map<String, String>? = null) {
	predicateConditions += BlockStateProperty(block, properties)
}

fun Predicate.blockStateProperty(block: BlockArgument, properties: MutableMap<String, String>.() -> Unit) {
	predicateConditions += BlockStateProperty(block, buildMap(properties))
}
