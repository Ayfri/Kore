package io.github.ayfri.kore.features.predicates.sub.item

import io.github.ayfri.kore.arguments.components.matchers.ComponentMatcher
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import kotlin.reflect.full.createType

@Serializable(with = ItemStackSubPredicates.Companion.ItemStackSubPredicatesSerializer::class)
data class ItemStackSubPredicates(
	var matchers: List<ComponentMatcher> = emptyList(),
) {
	companion object {
		data object ItemStackSubPredicatesSerializer : KSerializer<ItemStackSubPredicates> {
			private val matchersSerializer = MapSerializer(String.serializer(), ComponentMatcher.Companion.ComponentMatcherSerializer)
			override val descriptor = matchersSerializer.descriptor

			override fun serialize(encoder: Encoder, value: ItemStackSubPredicates) {
				encoder.encodeSerializableValue(
					matchersSerializer,
					value.matchers.associateBy { "minecraft:${it.componentName}" }
				)
			}

			@Suppress("UNCHECKED_CAST")
			override fun deserialize(decoder: Decoder): ItemStackSubPredicates {
				require(decoder is JsonDecoder) { "ItemStackSubPredicatesSerializer only supports JSON deserialization" }
				val jsonObject = decoder.decodeJsonElement() as JsonObject
				val matchers = jsonObject.map { (key, value) ->
					val typeName = key.removePrefix("minecraft:")
					val subclass = ComponentMatcher::class.sealedSubclasses.firstOrNull { subclass ->
						ComponentMatcher.getComponentName(subclass) == typeName
					} ?: error("No ComponentMatcher subclass found for '$key'")
					val serializer =
						decoder.json.serializersModule.serializer(subclass.createType()) as DeserializationStrategy<ComponentMatcher>
					decoder.json.decodeFromJsonElement(serializer, value)
				}
				return ItemStackSubPredicates(matchers)
			}
		}
	}
}
