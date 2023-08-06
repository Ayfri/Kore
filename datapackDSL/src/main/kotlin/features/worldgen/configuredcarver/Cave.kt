package features.worldgen.configuredcarver

import arguments.types.BlockOrTagArgument
import features.worldgen.floatproviders.FloatProvider
import features.worldgen.floatproviders.FloatProviderSurrogate
import features.worldgen.floatproviders.constant
import features.worldgen.heightproviders.HeightProvider
import features.worldgen.heightproviders.HeightProviderSurrogate
import features.worldgen.heightproviders.constantAbsolute
import features.worldgen.noisesettings.rules.conditions.HeightConstant
import features.worldgen.noisesettings.rules.conditions.absolute
import serializers.InlinableList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement

typealias NetherCave = Cave

@Serializable(with = Cave.Companion.CaveSerializer::class)
data class Cave(
	var probability: Double = 0.1,
	var y: HeightProvider = constantAbsolute(0),
	@SerialName("yScale")
	var yScale: FloatProvider = constant(0f),
	var lavaLevel: HeightConstant = absolute(0),
	var replaceable: InlinableList<BlockOrTagArgument> = emptyList(),
	var debugSettings: DebugSettings? = null,
	var horizontalRadiusMultiplier: FloatProvider = constant(0f),
	var verticalRadiusMultiplier: FloatProvider = constant(0f),
	var floorLevel: FloatProvider = constant(0f),
) : Config() {
	companion object {
		// Note : Workaround because @SerialName isn't taken into account by JsonNamingStrategy.
		object CaveSerializer : KSerializer<Cave> {
			override val descriptor = buildClassSerialDescriptor("Cave") {
				element<Double>("probability")
				element<HeightProvider>("y")
				element<FloatProvider>("yScale")
				element<HeightConstant>("lava_level")
				element<InlinableList<BlockOrTagArgument>>("replaceable")
				element<DebugSettings?>("debug_settings")
				element<FloatProvider>("horizontal_radius_multiplier")
				element<FloatProvider>("vertical_radius_multiplier")
				element<FloatProvider>("floor_level")
			}

			override fun deserialize(decoder: Decoder) = error("Cave is not deserializable")

			override fun serialize(encoder: Encoder, value: Cave) {
				require(encoder is JsonEncoder) { "Cave can only be serialized with Json" }

				val json = buildJsonObject {
					put("probability", encoder.json.encodeToJsonElement(value.probability))
					put("y", encoder.json.encodeToJsonElement(HeightProviderSurrogate.Companion.HeightProviderSerializer, value.y))
					put("yScale", encoder.json.encodeToJsonElement(FloatProviderSurrogate.Companion.FloatProviderSerializer, value.yScale))
					put("lava_level", encoder.json.encodeToJsonElement(HeightConstant.Companion.HeightConstantSerializer, value.lavaLevel))
					put("replaceable", encoder.json.encodeToJsonElement(value.replaceable))
					put("debug_settings", encoder.json.encodeToJsonElement(value.debugSettings))
					put(
						"horizontal_radius_multiplier",
						encoder.json.encodeToJsonElement(
							FloatProviderSurrogate.Companion.FloatProviderSerializer,
							value.horizontalRadiusMultiplier
						)
					)
					put(
						"vertical_radius_multiplier",
						encoder.json.encodeToJsonElement(
							FloatProviderSurrogate.Companion.FloatProviderSerializer,
							value.verticalRadiusMultiplier
						)
					)
					put(
						"floor_level",
						encoder.json.encodeToJsonElement(FloatProviderSurrogate.Companion.FloatProviderSerializer, value.floorLevel)
					)
				}

				encoder.encodeJsonElement(json)
			}
		}
	}
}

fun caveConfig(block: Cave.() -> Unit = {}) = Cave().apply(block)

fun netherCaveConfig(block: NetherCave.() -> Unit = {}) = NetherCave().apply(block)

fun Cave.debugSettings(block: DebugSettings.() -> Unit = {}) {
	debugSettings = DebugSettings().apply(block)
}
