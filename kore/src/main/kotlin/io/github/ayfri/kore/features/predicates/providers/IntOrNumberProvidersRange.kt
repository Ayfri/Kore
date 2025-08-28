package io.github.ayfri.kore.features.predicates.providers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = IntOrNumberProvidersRange.Companion.IntOrNumberProviderRangeSerializer::class)
data class IntOrNumberProvidersRange(
	var value: Int? = null,
	var range: Uniform? = null,
) {
	companion object {
		data object IntOrNumberProviderRangeSerializer : KSerializer<IntOrNumberProvidersRange> {
			override val descriptor = buildClassSerialDescriptor("IntOrIntNumberProvidersRange")

			override fun deserialize(decoder: Decoder) = error("IntOrIntNumberProvidersRange cannot be deserialized")

			override fun serialize(encoder: Encoder, value: IntOrNumberProvidersRange) = when {
				value.value != null -> encoder.encodeInt(value.value!!)
				value.range != null -> encoder.encodeSerializableValue(Uniform.serializer(), value.range!!)
				else -> error("IntOrIntNumberProvidersRange is empty")
			}
		}
	}
}

fun int(value: Int) = IntOrNumberProvidersRange(value)
fun providersRange(min: NumberProvider, max: NumberProvider) = IntOrNumberProvidersRange(range = uniform(min, max))
fun providersRange(min: Float, max: NumberProvider) = IntOrNumberProvidersRange(range = uniform(constant(min), max))
fun providersRange(min: NumberProvider, max: Float) = IntOrNumberProvidersRange(range = uniform(min, constant(max)))
fun intRange(min: Float, max: Float) = IntOrNumberProvidersRange(range = uniform(constant(min), constant(max)))
fun intRange(range: ClosedFloatingPointRange<Float>) =
	IntOrNumberProvidersRange(range = uniform(constant(range.start), constant(range.endInclusive)))
