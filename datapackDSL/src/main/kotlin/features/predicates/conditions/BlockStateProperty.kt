package features.predicates.conditions

import arguments.types.resources.BlockArgument
import features.advancements.states.State
import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class BlockStateProperty(
	var block: BlockArgument,
	var properties: Map<String, State<*>>? = null,
) : PredicateCondition

fun Predicate.blockStateProperty(block: BlockArgument, properties: Map<String, State<*>>? = null) {
	predicateConditions += BlockStateProperty(block, properties)
}

fun Predicate.blockStateProperty(block: BlockArgument, properties: Map<String, State<*>>.() -> Unit) {
	predicateConditions += BlockStateProperty(block, buildMap(properties))
}
