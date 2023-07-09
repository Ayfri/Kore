package features.recipes.data

import arguments.Argument
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import serializers.SingleOrMultiSerializer

typealias Ingredients = @Serializable(with = SingleOrMultiSerializer::class) MutableList<Ingredient>

@Serializable(with = Ingredient.Companion.IngredientSerializer::class)
data class Ingredient(
	var item: Argument.Item? = null,
	var tag: Argument.ItemTag? = null,
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
