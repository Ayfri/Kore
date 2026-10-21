package io.github.ayfri.kore.generation.fabric

import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

@KeepGeneratedSerializer
@Serializable(with = FabricAuthor.Companion.AuthorSerializer::class)
data class FabricAuthor(
	var name: String,
	var contact: FabricContact? = null,
) {
	companion object {
		data object AuthorSerializer : SinglePropertySimplifierSerializer<FabricAuthor>(generatedSerializer(), "name")
	}
}
