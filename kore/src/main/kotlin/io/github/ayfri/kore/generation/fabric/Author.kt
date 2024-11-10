package io.github.ayfri.kore.generation.fabric

import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Author.Companion.AuthorSerializer::class)
data class Author(
	var name: String,
	var contact: Contact? = null,
) {
	companion object {
		data object AuthorSerializer : SinglePropertySimplifierSerializer<Author, String>(
			kClass = Author::class,
			property = Author::name,
		)
	}
}
