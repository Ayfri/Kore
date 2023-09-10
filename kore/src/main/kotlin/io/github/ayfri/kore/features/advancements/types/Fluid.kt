package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.arguments.types.resources.FluidArgument
import io.github.ayfri.kore.features.advancements.states.State
import kotlinx.serialization.Serializable

@Serializable
data class Fluid(
	var fluid: FluidArgument? = null,
	var tag: String? = null,
	var state: Map<String, State<*>>? = null,
)

fun fluid(fluid: FluidArgument? = null, tag: String? = null, state: Map<String, State<*>>? = null) = Fluid(fluid, tag, state)
