package io.github.ayfri.kore.features.worldgen.biome.types

import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.serializers.splitNamespacedId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializer

/**
 * When serialized, this class will be serialized as a list of lists of strings (always 11 slots).
 * Null fields are serialized as empty lists `[]` to preserve positional order.
 */
@Serializable(with = Features.Companion.FeaturesSerializer::class)
data class Features(
	var rawGeneration: List<PlacedFeatureArgument>? = null,
	var lakes: List<PlacedFeatureArgument>? = null,
	var localModifications: List<PlacedFeatureArgument>? = null,
	var undergroundStructures: List<PlacedFeatureArgument>? = null,
	var surfaceStructures: List<PlacedFeatureArgument>? = null,
	var strongholds: List<PlacedFeatureArgument>? = null,
	var undergroundOres: List<PlacedFeatureArgument>? = null,
	var undergroundDecoration: List<PlacedFeatureArgument>? = null,
	var fluidSprings: List<PlacedFeatureArgument>? = null,
	var vegetalDecoration: List<PlacedFeatureArgument>? = null,
	var topLayerModification: List<PlacedFeatureArgument>? = null,
) {
	companion object {
		private fun Features.toList() = listOf(
			rawGeneration ?: emptyList(),
			lakes ?: emptyList(),
			localModifications ?: emptyList(),
			undergroundStructures ?: emptyList(),
			surfaceStructures ?: emptyList(),
			strongholds ?: emptyList(),
			undergroundOres ?: emptyList(),
			undergroundDecoration ?: emptyList(),
			fluidSprings ?: emptyList(),
			vegetalDecoration ?: emptyList(),
			topLayerModification ?: emptyList(),
		)

		private fun fromList(lists: List<List<PlacedFeatureArgument>>) = Features(
			rawGeneration = lists.getOrNull(0),
			lakes = lists.getOrNull(1),
			localModifications = lists.getOrNull(2),
			undergroundStructures = lists.getOrNull(3),
			surfaceStructures = lists.getOrNull(4),
			strongholds = lists.getOrNull(5),
			undergroundOres = lists.getOrNull(6),
			undergroundDecoration = lists.getOrNull(7),
			fluidSprings = lists.getOrNull(8),
			vegetalDecoration = lists.getOrNull(9),
			topLayerModification = lists.getOrNull(10),
		)

		data object FeaturesSerializer : KSerializer<Features> {
			private val elementSerializer = serializer<PlacedFeatureArgument>()
			private val listOfListsSerializer = ListSerializer(ListSerializer(elementSerializer))
			override val descriptor = listOfListsSerializer.descriptor

			override fun deserialize(decoder: Decoder): Features {
				val jsonDecoder = decoder as JsonDecoder
				val element = jsonDecoder.decodeJsonElement()

				fun placedFeature(content: String) = content.splitNamespacedId().let { (name, namespace) ->
					PlacedFeatureArgument(name, namespace)
				}

				if (element is JsonArray) {
					return fromList(element.map { entry ->
						when (entry) {
							is JsonArray -> entry.map { placedFeature(it.jsonPrimitive.content) }
							else -> listOf(placedFeature(entry.jsonPrimitive.content))
						}
					})
				}

				return fromList(listOf(listOf(placedFeature(element.jsonPrimitive.content))))
			}

			override fun serialize(encoder: Encoder, value: Features) {
				listOfListsSerializer.serialize(encoder, value.toList())
			}
		}
	}
}
