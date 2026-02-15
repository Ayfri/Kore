package io.github.ayfri.kore.features.worldgen.biome.types

import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.serializers.InlinableListSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializer

/**
 * When serialized, this class will be serialized as a list of lists of strings.
 * You should never skip a list if you want to define the value of a specific list, instead use `emptyList()` and never `null`.
 * Null lists are skipped breaking the order of the lists.
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
		private val ELEMENT_SERIALIZER = serializer<PlacedFeatureArgument>()
		private val INLINABLE_LIST_SERIALIZER = InlinableListSerializer(ELEMENT_SERIALIZER)

		private fun Features.toList() = listOfNotNull(
			rawGeneration,
			lakes,
			localModifications,
			undergroundStructures,
			surfaceStructures,
			strongholds,
			undergroundOres,
			undergroundDecoration,
			fluidSprings,
			vegetalDecoration,
			topLayerModification,
		)

		private fun fromList(lists: List<List<PlacedFeatureArgument>>): Features {
			val padded = lists + List(11 - lists.size) { null }
			return Features(
				rawGeneration = padded[0],
				lakes = padded[1],
				localModifications = padded[2],
				undergroundStructures = padded[3],
				surfaceStructures = padded[4],
				strongholds = padded[5],
				undergroundOres = padded[6],
				undergroundDecoration = padded[7],
				fluidSprings = padded[8],
				vegetalDecoration = padded[9],
				topLayerModification = padded[10],
			)
		}

		data object FeaturesSerializer : KSerializer<Features> {
			override val descriptor = ListSerializer(ListSerializer(ELEMENT_SERIALIZER)).descriptor

			override fun deserialize(decoder: Decoder): Features {
				val jsonDecoder = decoder as JsonDecoder
				val element = jsonDecoder.decodeJsonElement()

				if (element is JsonArray && element.all { it is JsonArray || it is kotlinx.serialization.json.JsonPrimitive }) {
					val lists = element.map { entry ->
						when (entry) {
							is JsonArray -> entry.map { PlacedFeatureArgument(it.jsonPrimitive.content) }
							else -> listOf(PlacedFeatureArgument(entry.jsonPrimitive.content))
						}
					}
					return fromList(lists)
				}

				// Single string element
				return fromList(listOf(listOf(PlacedFeatureArgument(element.jsonPrimitive.content))))
			}

			override fun serialize(encoder: Encoder, value: Features) {
				val features = value.toList()

				if (features.size == 1 && features[0].size == 1) {
					encoder.encodeSerializableValue(ELEMENT_SERIALIZER, features[0][0])
					return
				}

				encoder.encodeCollection(descriptor, features.size) {
					features.forEachIndexed { index, list ->
						encodeSerializableElement(descriptor, index, INLINABLE_LIST_SERIALIZER, list)
					}
				}
			}
		}
	}
}
