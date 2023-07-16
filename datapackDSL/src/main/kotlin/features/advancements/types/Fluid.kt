package features.advancements.types

import arguments.types.resources.FluidArgument
import features.advancements.states.State
import kotlinx.serialization.Serializable

@Serializable
data class Fluid(
	var fluid: FluidArgument? = null,
	var tag: String? = null,
	var state: Map<String, State<*>>? = null,
)

fun fluid(fluid: FluidArgument? = null, tag: String? = null, state: Map<String, State<*>>? = null) = Fluid(fluid, tag, state)
