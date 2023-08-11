package features.recipes.data

import arguments.types.resources.ItemArgument
import arguments.types.resources.tagged.ItemTagArgument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = Ingredient.Companion.IngredientSerializer::class)
data class Ingredient(
	var item: ItemArgument? = null,
	var tag: ItemTagArgument? = null,
) {
	companion object {
		object IngredientSerializer : KSerializer<Ingredient> {
			override val descriptor = buildClassSerialDescriptor("ingredient") {
				element<String>("item")
				element<String>("tag")
			}

			override fun deserialize(decoder: Decoder) = error("Ingredient deserialization not supported")

			override fun serialize(encoder: Encoder, value: Ingredient) = encoder.encodeStructure(descriptor) {
				value.item?.let { encodeStringElement(descriptor, 0, "${it.namespace}:${it.name.lowercase()}") }
				value.tag?.let { encodeStringElement(descriptor, 1, "${it.namespace}:${it.name.lowercase()}") }
			}
		}
	}
}
