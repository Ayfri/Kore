package io.github.ayfri.kore.features.worldgen.dimensiontype

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.arguments.TimelineOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.DimensionTypeArgument
import io.github.ayfri.kore.generated.arguments.types.WorldClockArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

/**
 * The rules of a world: its vertical bounds, its lighting, the mobs that can spawn in it and the environment
 * attributes it applies. A dimension points at one through its `type`.
 *
 * The defaults reproduce the vanilla overworld, so a dimension type only has to declare what it changes. Everything
 * that used to live in the removed `effects` and `natural` fields is now an [environment attribute][attributes].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/dimensions
 * Minecraft Wiki: https://minecraft.wiki/w/Dimension_type
 *
 * @property attributes The environment attributes overridden by the dimension, such as fog, music or mob behaviour.
 * @property hasSkylight Whether the sky lights the world, which also drives the day cycle and weather.
 * @property hasCeiling Whether the world has a bedrock ceiling, which affects light, maps and compasses.
 * @property hasEnderDragonFight Whether the ender dragon fight can happen in the world.
 * @property coordinateScale How coordinates are multiplied when travelling to this dimension, between `0.00001` and `30000000`.
 * @property ambientLight Minimum light level everywhere, from `0` (fully dark) to `1` (fully lit).
 * @property cardinalLight Which face of a block catches the most light, [CardinalLight.NETHER] flattening the shading.
 * @property defaultClock The world clock driving the day cycle, no clock meaning the time never advances.
 * @property hasFixedTime Whether the time of day is frozen, `false` by default.
 * @property logicalHeight How high chorus fruits and nether portals can bring a player, at most [height].
 * @property infiniburn The block tag listing the blocks that burn forever in the world.
 * @property minY The lowest buildable Y, between `-2032` and `2031`, and a multiple of `16`.
 * @property height How many blocks tall the world is, between `16` and `4064`, and a multiple of `16`.
 * @property monsterSpawnLightLevel The sky light levels at which monsters can spawn, from `0` to `15`.
 * @property monsterSpawnBlockLightLimit The block light level at or below which monsters can spawn, from `0` to `15`.
 * @property skybox Which sky is drawn above the world.
 * @property timelines The timelines running in the world, driving the sun, the moon and the day cycle.
 */
@Serializable
data class DimensionType(
	@Transient
	override var fileName: String = "dimension_type",
	var attributes: EnvironmentAttributesScope? = null,
	var hasSkylight: Boolean = true,
	var hasCeiling: Boolean = false,
	var hasEnderDragonFight: Boolean? = null,
	var coordinateScale: Double = 1.0,
	var ambientLight: Float = 0f,
	var cardinalLight: CardinalLight? = null,
	var defaultClock: WorldClockArgument? = null,
	var hasFixedTime: Boolean? = null,
	var logicalHeight: Int = 384,
	var infiniburn: InlinableList<BlockOrTagArgument> = listOf(Tags.Block.INFINIBURN_OVERWORLD),
	var minY: Int = -64,
	var height: Int = 384,
	var monsterSpawnLightLevel: IntProvider = ConstantIntProvider(0),
	var monsterSpawnBlockLightLimit: Int = 0,
	var skybox: SkyboxType? = null,
	var timelines: InlinableList<TimelineOrTagArgument>? = null,
) : Generator("dimension_type"), IntProviderScope {
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
 * Creates a dimension type, configured in [block].
 *
 * Everything left untouched keeps the vanilla overworld values, so a dimension type only has to declare what it
 * changes.
 *
 * ```kotlin
 * dimensionType("skylands_type") {
 *     ambientLight = 0.1f
 *     height = 256
 *     logicalHeight = 256
 *     minY = 0
 *     monsterSpawnLightLevel = uniform(0, 7)
 *     attributes {
 *         cloudHeight(192f)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/dimension_type/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/dimensions
 * Minecraft Wiki: https://minecraft.wiki/w/Dimension_type
 */
fun DataPack.dimensionType(
	fileName: String,
	block: DimensionType.() -> Unit = {},
): DimensionTypeArgument {
	val dimensionType = DimensionType(fileName).apply(block)
	dimensionTypes += dimensionType
	return DimensionTypeArgument(fileName, dimensionType.namespace ?: name)
}

/**
 * Overrides the environment attributes of the dimension, the fog, music and mob behaviour of the world.
 *
 * ```kotlin
 * dimensionType("nether_like") {
 *     attributes {
 *         fastLava(true)
 *         waterEvaporates(true)
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/environment-attributes
 */
fun DimensionType.attributes(init: EnvironmentAttributesScope.() -> Unit) {
	attributes = EnvironmentAttributesScope().apply(init)
}
