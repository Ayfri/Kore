package io.github.ayfri.kore.features.predicates.sub.item

import io.github.ayfri.kore.arguments.components.matchers.ComponentMatcher
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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
					value.matchers.associateBy { "minecraft:${it.componentName}" })
			}

			override fun deserialize(decoder: Decoder) = error("ItemComponentsMatchersHolder cannot be deserialized")
		}
	}
}
