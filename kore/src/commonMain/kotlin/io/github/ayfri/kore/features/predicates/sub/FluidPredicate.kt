package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.generated.arguments.FluidOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Matches a fluid and its fluid state, as the `fluid` key of a [LocationPredicate].
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class FluidPredicate(
	var fluids: InlinableList<FluidOrTagArgument>? = null,
	var state: Map<String, String>? = null,
)

/** Creates a [FluidPredicate] matching any of [fluids]. */
fun fluidPredicate(vararg fluids: FluidOrTagArgument, init: FluidPredicate.() -> Unit = {}) =
	FluidPredicate(fluids = fluids.toList().ifEmpty { null }).apply(init)

/** Restricts this predicate to the given [fluids]. */
fun FluidPredicate.fluids(vararg fluids: FluidOrTagArgument) {
	this.fluids = fluids.toList()
}

/** Requires all the given fluid [states]. */
fun FluidPredicate.states(vararg states: Pair<String, String>) {
	state = states.toMap()
}

/** Requires all the given fluid [states]. */
fun FluidPredicate.states(states: Map<String, String>) {
	state = states
}

/** Requires all the fluid states declared in [states]. */
fun FluidPredicate.states(states: MutableMap<String, String>.() -> Unit) {
	state = buildMap(states)
}
