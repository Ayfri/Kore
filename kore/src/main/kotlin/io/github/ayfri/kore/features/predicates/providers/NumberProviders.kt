package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.predicates.types.EntityType
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = NumberProvider.Companion.NumberProviderSerializer::class)
sealed class NumberProvider {
	companion object {
		data object NumberProviderSerializer : NamespacedPolymorphicSerializer<NumberProvider>(NumberProvider::class)
	}
}

@Serializable(with = Constant.Companion.ConstantNumberProviderSerializer::class)
data class Constant(val value: Float) : NumberProvider() {
	companion object {
		data object ConstantNumberProviderSerializer : InlineAutoSerializer<Constant>(Constant::class)
	}
}

@Serializable
data class Uniform(var min: NumberProvider, var max: NumberProvider) : NumberProvider()

@Serializable
data class Binomial(var n: NumberProvider, var p: NumberProvider) : NumberProvider()

@Serializable(ScoreTargetType.Companion.ScoreTargetTypeSerializer::class)
enum class ScoreTargetType {
	FIXED,
	CONTEXT;

	companion object {
		data object ScoreTargetTypeSerializer : ToStringSerializer<ScoreTargetType>({ "minecraft:${name.lowercase()}"})
	}
}

@Serializable(ScoreTargetNumberProvider.Companion.ScoreTargetNumberProviderSerializer::class)
data class ScoreTargetNumberProvider(
	var type: ScoreTargetType,
	var name: String? = null,
	var target: EntityType? = null,
) {
	companion object {
		data object ScoreTargetNumberProviderSerializer : KSerializer<ScoreTargetNumberProvider> {
			override val descriptor = buildClassSerialDescriptor("ScoreTargetNumberProvider") {
				element<String>("type")
				element<String>("name")
				element<String>("target")
			}

			override fun deserialize(decoder: Decoder) = error("ScoreTargetNumberProvider cannot be deserialized")

			override fun serialize(encoder: Encoder, value: ScoreTargetNumberProvider) {
				encoder.encodeStructure(descriptor) {
					encodeSerializableElement(descriptor, 0, ScoreTargetType.serializer(), value.type)

					when (value.type) {
						ScoreTargetType.FIXED -> encodeStringElement(descriptor, 1, value.name!!)
						else -> encodeSerializableElement(descriptor, 2, EntityType.serializer(), value.target!!)
					}
				}
			}
		}
	}
}

@Serializable
data class Score(
	var target: ScoreTargetNumberProvider,
	var score: String,
	var scale: Float? = null,
) : NumberProvider()

@Serializable
data class Storage(var storage: String, var path: String) : NumberProvider()

@Serializable
data class Enchantment(var amount: LevelBased) : NumberProvider()
