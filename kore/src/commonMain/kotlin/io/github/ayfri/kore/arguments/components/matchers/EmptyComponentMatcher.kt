package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection

/**
 * A component matcher that matches any value for a component, as long as the component exists.
 * This is used for existence checks like `{predicates:{instrument:{}}}`.
 *
 * @param componentType The name of the component type to check for existence.
 */
@Serializable(with = EmptyComponentMatcher.Companion.EmptyComponentMatcherSerializer::class)
data class EmptyComponentMatcher(
	val componentType: String,
) : ComponentMatcher() {
	override val componentName get() = componentType

	companion object {
		data object EmptyComponentMatcherSerializer : KSerializer<EmptyComponentMatcher> {
			override val descriptor = buildClassSerialDescriptor("EmptyComponentMatcher")

			@OptIn(ExperimentalSerializationApi::class)
			override fun serialize(encoder: Encoder, value: EmptyComponentMatcher) =
				encoder.encodeCollection(mapSerialDescriptor<String, String>(), 0) {}

			override fun deserialize(decoder: Decoder) = error("EmptyComponentMatcher cannot be deserialized")
		}
	}
}

/**
 * Check if a component exists on an item, without checking its value.
 * This is equivalent to `{predicates:{<component>:{}}}` in Minecraft.
 *
 * Example:
 * ```kotlin
 * predicates {
 *     exists(ItemComponentTypes.INSTRUMENT)
 * }
 * ```
 * Will output: `{predicates:{"minecraft:instrument":{}}}`
 */
fun ItemStackSubPredicates.exists(component: ItemComponentTypes) = exists(component.name.lowercase())

/**
 * Check if a component exists on an item, without checking its value.
 * This is equivalent to `{predicates:{<component>:{}}}` in Minecraft.
 *
 * Example:
 * ```kotlin
 * predicates {
 *     exists("instrument")
 * }
 * ```
 * Will output: `{predicates:{"minecraft:instrument":{}}}`
 */
fun ItemStackSubPredicates.exists(componentName: String) = apply {
	matchers += EmptyComponentMatcher(componentName)
}
