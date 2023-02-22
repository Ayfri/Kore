package features.predicates.conditions

import arguments.Argument
import features.advancements.states.State
import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class BlockStateProperty(
	var block: Argument.Block,
	var properties: Map<String, State<*>>? = null,
) : PredicateCondition

fun Predicate.blockStateProperty(block: Argument.Block, properties: Map<String, State<*>>? = null) {
	predicateConditions += BlockStateProperty(block, properties)
}

fun Predicate.blockStateProperty(block: Argument.Block, properties: Map<String, State<*>>.() -> Unit) {
	predicateConditions += BlockStateProperty(block, buildMap(properties))
}
