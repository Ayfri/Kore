package io.github.ayfri.kore.features.predicates.sub

/** Something matching block or fluid state properties through a `state` map, like [BlockPredicate] or [FluidPredicate]. */
interface StateHolder {
	var state: Map<String, String>?
}

/** Requires the state property [key] to equal [value]. */
fun StateHolder.state(key: String, value: String) {
	state = mapOf(key to value)
}

/** Requires all the given state properties. */
fun StateHolder.states(vararg states: Pair<String, String>) {
	state = states.toMap()
}

/** Requires all the given state properties. */
fun StateHolder.states(states: Map<String, String>) {
	state = states
}

/** Requires all the state properties declared in [block]. */
fun StateHolder.states(block: MutableMap<String, String>.() -> Unit) {
	state = buildMap(block)
}
