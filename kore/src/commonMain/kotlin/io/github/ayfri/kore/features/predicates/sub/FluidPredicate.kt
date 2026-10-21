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
	override var state: Map<String, String>? = null,
) : StateHolder

/** Creates a [FluidPredicate] matching any of [fluids]. */
fun fluidPredicate(vararg fluids: FluidOrTagArgument, init: FluidPredicate.() -> Unit = {}) =
	FluidPredicate(fluids = fluids.toList().ifEmpty { null }).apply(init)

/** Restricts this predicate to the given [fluids]. */
fun FluidPredicate.fluids(vararg fluids: FluidOrTagArgument) {
	this.fluids = fluids.toList()
}
