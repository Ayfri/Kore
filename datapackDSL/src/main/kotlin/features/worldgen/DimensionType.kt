package features.worldgen

import DataPack
import Generator
import arguments.Argument
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import java.io.File


/**
 * Creates a new DimensionType.
 * Values are the minimal values for a dimension type, booleans have the same values as overworld.
 */
@Serializable
data class DimensionType(
	@Transient var fileName: String = "dimension_type",
	var ultrawarm: Boolean = false,
	var natural: Boolean = true,
	var coordinateScale: Double = 1.0,
	var hasSkylight: Boolean = true,
	var hasCeiling: Boolean = false,
	var ambientLight: Float = 0f,
	var fixedTime: Long? = null,
	var monsterSpawnLightLevel: IntProvider = constant(0),
	var monsterSpawnBlockLightLimit: Int = 0,
	var piglinSafe: Boolean = false,
	var bedWorks: Boolean = true,
	var respawnAnchorWorks: Boolean = false,
	var hasRaids: Boolean = true,
	var logicalHeight: Int = 0,
	var minY: Int = 0,
	var height: Int = 16,
	var infiniburn: Argument.BlockTag,
	var effects: Argument.Dimension? = null,
) : Generator {
	override fun generate(dataPack: DataPack, directory: File) {
		val json = dataPack.jsonEncoder.encodeToString(this)
		File(directory, "$fileName.json").writeText(json)
	}
}
