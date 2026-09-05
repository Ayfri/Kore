package io.github.ayfri.kore.arguments.components.matchers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject

/**
 * Tests data component values without requiring an exact match, as the `predicates` key of an item, block or entity
 * predicate. Each [ComponentMatcher] is written under the component type it tests.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable(with = DataComponentPredicate.Companion.DataComponentPredicateSerializer::class)
data class DataComponentPredicate(
	var matchers: List<ComponentMatcher> = emptyList(),
) {
	companion object {
		data object DataComponentPredicateSerializer : KSerializer<DataComponentPredicate> {
			private val matchersSerializer = MapSerializer(String.serializer(), ComponentMatcher.Companion.ComponentMatcherSerializer)
			override val descriptor = matchersSerializer.descriptor

			override fun serialize(encoder: Encoder, value: DataComponentPredicate) {
				encoder.encodeSerializableValue(
					matchersSerializer,
					value.matchers.associateBy { "minecraft:${it.componentName}" }
				)
			}

			override fun deserialize(decoder: Decoder): DataComponentPredicate {
				require(decoder is JsonDecoder) { "DataComponentPredicate only supports JSON deserialization" }
				val jsonObject = decoder.decodeJsonElement() as JsonObject
				return DataComponentPredicate(jsonObject.map { (key, value) ->
					ComponentMatcher.Companion.ComponentMatcherSerializer.deserializeJsonElement(decoder.json, key, value)
				})
			}
		}
	}
}
