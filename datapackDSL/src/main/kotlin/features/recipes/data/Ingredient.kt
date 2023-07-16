package features.recipes.data

import arguments.types.resources.ItemArgument
import arguments.types.resources.tagged.ItemTagArgument
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = Ingredient.Companion.IngredientSerializer::class)
data class Ingredient(
	var item: ItemArgument? = null,
	var tag: ItemTagArgument? = null,
) {
	companion object {
		@OptIn(ExperimentalSerializationApi::class)
		@Serializer(forClass = Ingredient::class)
		data object DefaultIngredientSerializer

		object IngredientSerializer : KSerializer<Ingredient> by DefaultIngredientSerializer {
			override fun serialize(encoder: Encoder, value: Ingredient) {
				encoder.encodeStructure(descriptor) {
					value.item?.let { encodeStringElement(descriptor, 0, "${it.namespace}:${it.name.lowercase()}") }
					value.tag?.let { encodeStringElement(descriptor, 1, "${it.namespace}:${it.name.lowercase()}") }
				}
			}
		}
	}
}
