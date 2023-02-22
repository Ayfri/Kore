package features.advancements.types

import arguments.Argument
import features.advancements.states.State
import kotlinx.serialization.Serializable

@Serializable
data class Fluid(
	var fluid: Argument.Fluid? = null,
	var tag: String? = null,
	var state: Map<String, State<*>>? = null,
)

fun fluid(fluid: Argument.Fluid? = null, tag: String? = null, state: Map<String, State<*>>? = null) = Fluid(fluid, tag, state)
