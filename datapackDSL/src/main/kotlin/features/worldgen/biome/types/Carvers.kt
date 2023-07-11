package features.worldgen.biome.types

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class Carvers(
	var air: List<Argument.Carver>? = null,
	var liquid: List<Argument.Carver>? = null
)

fun Carvers.air(init: MutableList<Argument.Carver>.() -> Unit) {
	air = buildList(init)
}

fun Carvers.air(vararg values: Argument.Carver) {
	air = values.toList()
}

fun Carvers.liquid(init: MutableList<Argument.Carver>.() -> Unit) {
	liquid = buildList(init)
}

fun Carvers.liquid(vararg values: Argument.Carver) {
	liquid = values.toList()
}
