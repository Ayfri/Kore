package features.worldgen.biome.types

import arguments.Argument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.serializer

/**
 * When serialized, this class will be serialized as a list of lists of strings.
 * You should never skip a list if you want to define the value of a specific list, instead use `emptyList()` and never `null`.
 * Null lists are skipped breaking the order of the lists.
 */
@Serializable(with = Features.Companion.FeaturesSerializer::class)
data class Features(
	var rawGeneration: List<Argument.Feature>? = null,
	var lakes: List<Argument.Feature>? = null,
	var localModifications: List<Argument.Feature>? = null,
	var undergroundStructures: List<Argument.Feature>? = null,
	var surfaceStructures: List<Argument.Feature>? = null,
	var strongholds: List<Argument.Feature>? = null,
	var undergroundOres: List<Argument.Feature>? = null,
	var undergroundDecoration: List<Argument.Feature>? = null,
	var fluidSprings: List<Argument.Feature>? = null,
	var vegetalDecoration: List<Argument.Feature>? = null,
	var topLayerModification: List<Argument.Feature>? = null
) {
	companion object {
		object FeaturesSerializer : KSerializer<Features> {
			override val descriptor = ListSerializer(ListSerializer(serializer<Argument.Feature>())).descriptor

			override fun deserialize(decoder: Decoder) = error("BiomeFeatures deserialization not supported")

			override fun serialize(encoder: Encoder, value: Features) {
				val features = listOfNotNull(
					value.rawGeneration,
					value.lakes,
					value.localModifications,
					value.undergroundStructures,
					value.surfaceStructures,
					value.strongholds,
					value.undergroundOres,
					value.undergroundDecoration,
					value.fluidSprings,
					value.vegetalDecoration,
					value.topLayerModification
				)

				val elementSerializer = serializer<Argument.Feature>()
				val listSerializer = ListSerializer(elementSerializer)

				if (features.size == 1 && features[0].size == 1) {
					encoder.encodeSerializableValue(elementSerializer, features[0][0])
					return
				}

				encoder.encodeCollection(descriptor, features.size) {
					features.forEachIndexed { index, it ->
						when (it.size) {
							1 -> encodeSerializableElement(descriptor, index, elementSerializer, it[0])
							else -> encodeSerializableElement(descriptor, index, listSerializer, it)
						}
					}
				}
			}
		}
	}
}
