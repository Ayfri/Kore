package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.types.FluidOrTagArgument
import io.github.ayfri.kore.features.advancements.states.State
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Fluid(
	var fluids: InlinableList<FluidOrTagArgument>? = null,
	var state: Map<String, State<*>>? = null,
)

fun fluid(fluid: FluidOrTagArgument, state: Map<String, State<*>>? = null) = Fluid(listOf(fluid), state)
fun fluid(fluids: FluidOrTagArgument, states: MutableMap<String, State<*>>.() -> Unit) = Fluid(listOf(fluids), buildMap(states))

fun fluids(vararg fluids: FluidOrTagArgument, state: Map<String, State<*>>? = null) = Fluid(fluids.toList(), state)
fun fluids(vararg fluids: FluidOrTagArgument, states: MutableMap<String, State<*>>.() -> Unit) = Fluid(fluids.toList(), buildMap(states))
