package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.generated.arguments.FluidOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Fluid(
	var fluids: InlinableList<FluidOrTagArgument>? = null,
	var state: Map<String, String>? = null,
)

fun Fluid.states(vararg states: Pair<String, String>) {
	state = states.toMap()
}

fun Fluid.states(states: Map<String, String>) {
	state = states
}

fun Fluid.states(states: MutableMap<String, String>.() -> Unit) {
	state = buildMap(states)
}
