package features.worldgen

import DataPack
import Generator
import arguments.Argument
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
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
	@Transient
	private lateinit var jsonEncoder: Json

	override fun generate(dataPack: DataPack, directory: File) {
		val json = getJsonEncoder(dataPack).encodeToString(this)
		File(directory, "$fileName.json").writeText(json)
	}

	@OptIn(ExperimentalSerializationApi::class)
	fun getJsonEncoder(dataPack: DataPack) = when {
		::jsonEncoder.isInitialized -> jsonEncoder
		else -> {
			jsonEncoder = Json {
				prettyPrint = dataPack.jsonEncoder.configuration.prettyPrint
				if (prettyPrint) prettyPrintIndent = dataPack.jsonEncoder.configuration.prettyPrintIndent
				namingStrategy = JsonNamingStrategy.SnakeCase
				encodeDefaults = true
				explicitNulls = false
			}
			jsonEncoder
		}
	}
}
