package features.worldgen.processorlist.types

import features.worldgen.HeightMap
import features.worldgen.processorlist.ProcessorList
import kotlinx.serialization.Serializable

@Serializable
data class Gravity(
	var heightmap: HeightMap,
	var offset: Int = 0,
) : ProcessorType()

fun ProcessorList.gravity(heightmap: HeightMap, offset: Int = 0) {
	processors += Gravity(heightmap, offset)
}
