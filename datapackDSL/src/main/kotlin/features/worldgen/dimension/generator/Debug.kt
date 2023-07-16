package features.worldgen.dimension.generator

import features.worldgen.dimension.Dimension
import kotlinx.serialization.Serializable

@Serializable
data object Debug : Generator()

fun Dimension.debugGenerator() {
	generator = Debug
}
