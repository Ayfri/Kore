package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.features.worldgen.HeightMap
import io.github.ayfri.kore.features.worldgen.processorlist.ProcessorList
import kotlinx.serialization.Serializable

@Serializable
data class Gravity(
	var heightmap: HeightMap,
	var offset: Int = 0,
) : ProcessorType()

fun ProcessorList.gravity(heightmap: HeightMap, offset: Int = 0) {
	processors += Gravity(heightmap, offset)
}
