package features.worldgen.biome.types

import arguments.types.resources.worldgen.CarverArgument
import kotlinx.serialization.Serializable

@Serializable
data class Carvers(
	var air: List<CarverArgument>? = null,
	var liquid: List<CarverArgument>? = null
)

fun Carvers.air(init: MutableList<CarverArgument>.() -> Unit) {
	air = buildList(init)
}

fun Carvers.air(vararg values: CarverArgument) {
	air = values.toList()
}

fun Carvers.liquid(init: MutableList<CarverArgument>.() -> Unit) {
	liquid = buildList(init)
}

fun Carvers.liquid(vararg values: CarverArgument) {
	liquid = values.toList()
}
