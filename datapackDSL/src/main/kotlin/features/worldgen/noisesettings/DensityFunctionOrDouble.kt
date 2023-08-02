package features.worldgen.noisesettings

import arguments.types.resources.worldgen.DensityFunctionArgument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

/**
 * This class represents a density function or a double value.
 * Needed because of Minecraft [NoiseRouter] properties.
 *
 * @property double The double value. It can be null if the instance represents a density function.
 * @property densityFunction The density function argument. It can be null if the instance represents a double value.
 */
@Serializable(with = DensityFunctionOrDouble.Companion.DensityFunctionOrDoubleSerializer::class)
data class DensityFunctionOrDouble(
	var double: Double? = null,
	var densityFunction: DensityFunctionArgument? = null,
) {
	companion object {
		object DensityFunctionOrDoubleSerializer : KSerializer<DensityFunctionOrDouble> {
			override val descriptor = buildClassSerialDescriptor("DensityFunctionOrDouble")

			override fun deserialize(decoder: Decoder) = error("DensityFunctionOrDouble is not deserializable")

			override fun serialize(encoder: Encoder, value: DensityFunctionOrDouble) = when {
				value.double != null -> encoder.encodeDouble(value.double!!)
				value.densityFunction != null -> encoder.encodeSerializableValue(
					serializer<DensityFunctionArgument>(),
					value.densityFunction!!
				)

				else -> error("DensityFunctionOrDouble must have either a reference or an inline value")
			}
		}
	}
}

fun densityFunctionOrDouble(double: Double) = DensityFunctionOrDouble(double = double)
fun densityFunctionOrDouble(densityFunction: DensityFunctionArgument) = DensityFunctionOrDouble(densityFunction = densityFunction)
