package io.github.ayfri.kore.features.worldgen.dimension.generator

import io.github.ayfri.kore.features.worldgen.dimension.Dimension
import kotlinx.serialization.Serializable

@Serializable
data object Debug : Generator()

fun Dimension.debugGenerator() {
	generator = Debug
}
