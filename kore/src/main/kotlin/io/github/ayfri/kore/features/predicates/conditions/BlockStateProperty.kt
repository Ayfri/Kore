package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.advancements.states.State
import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class BlockStateProperty(
	var block: BlockArgument,
	var properties: Map<String, State<*>>? = null,
) : PredicateCondition()

fun Predicate.blockStateProperty(block: BlockArgument, properties: Map<String, State<*>>? = null) {
	predicateConditions += BlockStateProperty(block, properties)
}

fun Predicate.blockStateProperty(block: BlockArgument, properties: MutableMap<String, State<*>>.() -> Unit) {
	predicateConditions += BlockStateProperty(block, buildMap(properties))
}
