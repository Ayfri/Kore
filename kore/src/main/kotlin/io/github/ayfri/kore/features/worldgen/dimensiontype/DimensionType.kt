package io.github.ayfri.kore.features.worldgen.dimensiontype

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.tagged.BlockTagArgument
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.arguments.TimelineOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.DimensionTypeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

/**
 * Data-driven dimension type definition.
 *
 * Controls fundamental world rules for a dimension: skylight, ceilings, piglin safety, respawn
 * mechanics, coordinate scaling, ambient light, height range, infiniburn tag, and monster spawn
 * light rules. This is referenced by a `dimension`.
 *
 * JSON format reference: https://minecraft.wiki/w/Dimension_type
 */
@Serializable
data class DimensionType(
	@Transient
	override var fileName: String = "dimension_type",
	var attributes: EnvironmentAttributesScope? = null,
	var natural: Boolean = true,
	var hasSkylight: Boolean = true,
	var hasCeiling: Boolean = false,
	var coordinateScale: Double = 1.0,
	var ambientLight: Float = 0f,
	var cardinalLight: CardinalLight? = null,
	var hasFixedTime: Boolean? = null,
	var logicalHeight: Int = 0,
	var infiniburn: BlockTagArgument,
	var minY: Int = 0,
	var height: Int = 16,
	var monsterSpawnLightLevel: IntProvider = constant(0),
	var monsterSpawnBlockLightLimit: Int = 0,
	var skybox: SkyboxType? = null,
	var timelines: InlinableList<TimelineOrTagArgument>? = null,
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
 * Creates a dimension type using a builder block.
 *
 * Pre-fills infiniburn with the Overworld tag; adjust fields as needed.
 *
 * Produces `data/<namespace>/dimension_type/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/Dimension_type
 */
fun DataPack.dimensionType(
	fileName: String,
	block: DimensionType.() -> Unit = {},
): DimensionTypeArgument {
	val dimensionType = DimensionType(fileName, infiniburn = Tags.Block.INFINIBURN_OVERWORLD).apply(block)
	dimensionTypes += dimensionType
	return DimensionTypeArgument(fileName, dimensionType.namespace ?: name)
}

fun DimensionType.attributes(init: EnvironmentAttributesScope.() -> Unit) {
	attributes = EnvironmentAttributesScope().apply(init)
}
