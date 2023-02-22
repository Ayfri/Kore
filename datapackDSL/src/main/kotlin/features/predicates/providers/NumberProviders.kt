package features.predicates.providers

import arguments.numbers.FloatRangeOrFloat
import features.predicates.types.EntityType
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import serializers.LowercaseSerializer
import serializers.ToStringSerializer

@Serializable
sealed interface NumberProvider

@JvmInline
@Serializable
value class ConstantNumberProvider(
	val value: Float,
) : NumberProvider

@Serializable
data class UniformNumberProvider(
	var min: FloatRangeOrFloat,
	var max: FloatRangeOrFloat,
) : NumberProvider

@Serializable
data class BinomialNumberProvider(
	var n: Int,
	var p: Float,
) : NumberProvider

@Serializable(ScoreTargetType.Companion.ScoreTargetTypeSerializer::class)
enum class ScoreTargetType {
	FIXED,
	CONTEXT;

	companion object {
		val values = values()

		object ScoreTargetTypeSerializer : LowercaseSerializer<ScoreTargetType>(values)
	}
}

@Serializable(ScoreTargetNumberProvider.Companion.ScoreTargetNumberProviderSerializer::class)
data class ScoreTargetNumberProvider(
	var type: ScoreTargetType,
	var name: String? = null,
	var target: EntityType? = null,
) {
	companion object {
		object ScoreTargetNumberProviderSerializer : ToStringSerializer<ScoreTargetNumberProvider>() {
			override val descriptor = buildClassSerialDescriptor("ScoreTargetNumberProvider") {
				element<String>("type")
				element<String>("name")
				element<String>("target")
			}

			override fun serialize(encoder: Encoder, value: ScoreTargetNumberProvider) {
				encoder.encodeStructure(descriptor) {
					encodeStringElement(descriptor, 0, "minecraft:${value.type.name}")
					if (value.type == ScoreTargetType.FIXED) {
						encodeStringElement(descriptor, 1, value.name!!)
					} else {
						encodeStringElement(descriptor, 2, value.target!!.name)
					}
				}
			}
		}
	}
}

@Serializable
data class ScoreNumberProvider(
	var target: ScoreTargetNumberProvider,
	var score: String,
	var scale: Float? = null,
) : NumberProvider
