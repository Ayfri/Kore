package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.predicates.types.EntityType
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
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
		data object ScoreTargetNumberProviderSerializer : KSerializer<ScoreTargetNumberProvider> {
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
data class Score(
	var target: ScoreTargetNumberProvider,
	var score: String,
	var scale: Float? = null,
) : NumberProvider()

@Serializable
data class Storage(var storage: String, var path: String) : NumberProvider()

@Serializable
data class Enchantment(var amount: LevelBased) : NumberProvider()
