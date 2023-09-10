package io.github.ayfri.kore.features.worldgen.dimensiontype

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.tagged.BlockTagArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.DimensionArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.DimensionTypeArgument
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.Tags
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Creates a new DimensionType.
 * Values are the minimal values for a dimension type, booleans have the same values as overworld.
 */
@Serializable
data class DimensionType(
	@Transient
	override var fileName: String = "dimension_type",
	var ultrawarm: Boolean = false,
	var natural: Boolean = true,
	var piglinSafe: Boolean = false,
	var respawnAnchorWorks: Boolean = false,
	var bedWorks: Boolean = true,
	var hasRaids: Boolean = true,
	var hasSkylight: Boolean = true,
	var hasCeiling: Boolean = false,
	var coordinateScale: Double = 1.0,
	var ambientLight: Float = 0f,
	var logicalHeight: Int = 0,
	var fixedTime: Long? = null,
	var effects: DimensionArgument? = null,
	var infiniburn: BlockTagArgument,
	var minY: Int = 0,
	var height: Int = 16,
	var monsterSpawnLightLevel: IntProvider = constant(0),
	var monsterSpawnBlockLightLimit: Int = 0,
) : Generator("dimension_type") {
	@Transient
	private lateinit var jsonEncoder: Json

	override fun generateJson(dataPack: DataPack) = getJsonEncoder(dataPack).encodeToString(this)

	@OptIn(ExperimentalSerializationApi::class)
	fun getJsonEncoder(dataPack: DataPack) = when {
		::jsonEncoder.isInitialized -> jsonEncoder
		else -> {
			jsonEncoder = Json {
				prettyPrint = dataPack.jsonEncoder.configuration.prettyPrint
				if (prettyPrint) prettyPrintIndent = dataPack.jsonEncoder.configuration.prettyPrintIndent
				namingStrategy = dataPack.jsonEncoder.configuration.namingStrategy
				encodeDefaults = true
				explicitNulls = false
			}
			jsonEncoder
		}
	}
}

/**
 * Creates a new DimensionType.
 * Values are the minimal values for a dimension type, booleans have the same values as overworld.
 */
fun DataPack.dimensionType(fileName: String = "dimension_type", dimensionType: DimensionType.() -> Unit = {}): DimensionTypeArgument {
	dimensionTypes += DimensionType(fileName, infiniburn = Tags.Blocks.INFINIBURN_OVERWORLD).apply(dimensionType)
	return DimensionTypeArgument(fileName, name)
}
