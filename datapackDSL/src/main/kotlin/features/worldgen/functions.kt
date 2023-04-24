package features.worldgen

import DataPack
import generated.Tags

/**
 * Creates a new DimensionType.
 * Values are the minimal values for a dimension type, booleans have the same values as overworld.
 */
fun DataPack.dimensionType(fileName: String, dimensionType: DimensionType.() -> Unit = {}): DimensionType {
	val dimensionType = DimensionType(infiniburn = Tags.Blocks.INFINIBURN_OVERWORLD).apply(dimensionType)
	dimensionType.fileName = fileName
	dimensionTypes += dimensionType
	return dimensionType
}
