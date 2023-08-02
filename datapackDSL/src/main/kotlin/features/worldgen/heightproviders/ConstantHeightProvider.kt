package features.worldgen.heightproviders

import features.worldgen.noisesettings.rules.conditions.AboveBottom
import features.worldgen.noisesettings.rules.conditions.Absolute
import features.worldgen.noisesettings.rules.conditions.BelowTop
import features.worldgen.noisesettings.rules.conditions.HeightConstant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ConstantHeightProvider.Companion.ConstantSerializer::class)
@SerialName("minecraft:constant")
data class ConstantHeightProvider(
	var value: HeightConstant,
) : HeightProvider() {
	companion object {
		object ConstantSerializer : KSerializer<ConstantHeightProvider> {
			override val descriptor = buildClassSerialDescriptor("constant")

			override fun deserialize(decoder: Decoder) = error("Constant cannot be deserialized")

			override fun serialize(encoder: Encoder, value: ConstantHeightProvider) = when (value.value) {
				is Absolute -> encoder.encodeSerializableValue(Absolute.serializer(), value.value as Absolute)
				is AboveBottom -> encoder.encodeSerializableValue(AboveBottom.serializer(), value.value as AboveBottom)
				is BelowTop -> encoder.encodeSerializableValue(BelowTop.serializer(), value.value as BelowTop)
			}
		}
	}
}

fun constant(value: HeightConstant) = ConstantHeightProvider(value)
fun constantAbsolute(absolute: Int) = ConstantHeightProvider(Absolute(absolute))
fun constantAboveBottom(aboveBottom: Int) = ConstantHeightProvider(AboveBottom(aboveBottom))
fun constantBelowTop(belowTop: Int) = ConstantHeightProvider(BelowTop(belowTop))
