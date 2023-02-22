package features.predicates

import arguments.Argument
import features.advancements.states.State
import kotlinx.serialization.Serializable

@Serializable
data class BlockStateProperty(
	var block: Argument.Block,
	var properties: Map<String, State<*>>? = null,
) : Predicate
