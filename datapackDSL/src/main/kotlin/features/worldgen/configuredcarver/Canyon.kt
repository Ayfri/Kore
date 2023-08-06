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

@Serializable(with = Canyon.Companion.CanyonSerializer::class)
data class Canyon(
	var probability: Double = 0.1,
	var y: HeightProvider = constantAbsolute(0),
	@SerialName("yScale")
	var yScale: FloatProvider = constant(0f),
	var lavaLevel: HeightConstant = absolute(0),
	var replaceable: InlinableList<BlockOrTagArgument> = emptyList(),
	var debugSettings: DebugSettings? = null,
	var verticalRotation: FloatProvider = constant(0f),
	var shape: CanyonShapeConfig = CanyonShapeConfig(),
) : Config() {
	companion object {
		object CanyonSerializer : KSerializer<Canyon> {
			override val descriptor = buildClassSerialDescriptor("Canyon") {
				element<Double>("probability")
				element<HeightProvider>("y")
				element<FloatProvider>("yScale")
				element<HeightConstant>("lava_level")
				element<InlinableList<BlockOrTagArgument>>("replaceable")
				element<DebugSettings?>("debug_settings")
				element<FloatProvider>("vertical_rotation")
				element<CanyonShapeConfig>("shape")
			}

			override fun deserialize(decoder: Decoder) = error("Canyon is not deserializable")

			override fun serialize(encoder: Encoder, value: Canyon) {
				require(encoder is JsonEncoder) { "Canyon can only be serialized with Json" }

				val json = buildJsonObject {
					put("probability", encoder.json.encodeToJsonElement(value.probability))
					put("y", encoder.json.encodeToJsonElement(HeightProviderSurrogate.Companion.HeightProviderSerializer, value.y))
					put("yScale", encoder.json.encodeToJsonElement(FloatProviderSurrogate.Companion.FloatProviderSerializer, value.yScale))
					put("lava_level", encoder.json.encodeToJsonElement(HeightConstant.Companion.HeightConstantSerializer, value.lavaLevel))
					put("replaceable", encoder.json.encodeToJsonElement(value.replaceable))
					put("debug_settings", encoder.json.encodeToJsonElement(value.debugSettings))
					put(
						"vertical_rotation",
						encoder.json.encodeToJsonElement(FloatProviderSurrogate.Companion.FloatProviderSerializer, value.verticalRotation)
					)
					put("shape", encoder.json.encodeToJsonElement(value.shape))
				}

				encoder.encodeJsonElement(json)
			}
		}
	}
}

@Serializable
data class CanyonShapeConfig(
	var distanceFactor: FloatProvider = constant(0f),
	var thickness: FloatProvider = constant(0f),
	var widthSmoothness: Float = 0f,
	var horizontalRadiusMultiplier: FloatProvider = constant(0f),
	var verticalRadiusMultiplier: Float = 0f,
	var floorLevel: Float = 0f,
)

fun canyonConfig(block: Canyon.() -> Unit = {}) = Canyon().apply(block)

fun Canyon.debugSettings(block: DebugSettings.() -> Unit = {}) {
	debugSettings = DebugSettings().apply(block)
}

fun Canyon.shape(block: CanyonShapeConfig.() -> Unit = {}) {
	shape = CanyonShapeConfig().apply(block)
}
