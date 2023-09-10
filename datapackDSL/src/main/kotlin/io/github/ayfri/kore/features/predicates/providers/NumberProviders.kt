package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.predicates.types.EntityType
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.serializers.ProviderSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

typealias NumberProvider = @Serializable(with = NumberProviderSurrogate.Companion.NumberProviderSerializer::class) NumberProviderSurrogate

@Serializable
sealed interface NumberProviderSurrogate {
	companion object {
		data object NumberProviderSerializer : ProviderSerializer<NumberProvider>(serializer())
	}
}

@Serializable
@SerialName("minecraft:constant")
data class ConstantNumberProvider(
	val value: Float,
) : NumberProviderSurrogate

@Serializable
@SerialName("minecraft:uniform")
data class UniformNumberProvider(
	var min: NumberProvider,
	var max: NumberProvider,
) : NumberProviderSurrogate

@Serializable
@SerialName("minecraft:binomial")
data class BinomialNumberProvider(
	var n: NumberProvider,
	var p: NumberProvider,
) : NumberProviderSurrogate

@Serializable(ScoreTargetType.Companion.ScoreTargetTypeSerializer::class)
enum class ScoreTargetType {
	FIXED,
	CONTEXT;

	companion object {
		data object ScoreTargetTypeSerializer : LowercaseSerializer<ScoreTargetType>(entries)
	}
}

@Serializable(ScoreTargetNumberProvider.Companion.ScoreTargetNumberProviderSerializer::class)
data class ScoreTargetNumberProvider(
	var type: ScoreTargetType,
	var name: String? = null,
	var target: EntityType? = null,
) {
	companion object {
		object ScoreTargetNumberProviderSerializer : KSerializer<ScoreTargetNumberProvider> {
			override val descriptor = buildClassSerialDescriptor("ScoreTargetNumberProvider") {
				element<String>("type")
				element<String>("name")
				element<String>("target")
			}

			override fun deserialize(decoder: Decoder) = error("ScoreTargetNumberProvider cannot be deserialized")

			override fun serialize(encoder: Encoder, value: ScoreTargetNumberProvider) {
				encoder.encodeStructure(descriptor) {
					encodeStringElement(descriptor, 0, "minecraft:${value.type.name}")

					val (position, targetValue) = when (value.type) {
						ScoreTargetType.FIXED -> 1 to value.name!!
						else -> 2 to value.target!!.name
					}
					encodeStringElement(descriptor, position, targetValue)
				}
			}
		}
	}
}

@Serializable
@SerialName("minecraft:score")
data class ScoreNumberProvider(
	var target: ScoreTargetNumberProvider,
	var score: String,
	var scale: Float? = null,
) : NumberProviderSurrogate
