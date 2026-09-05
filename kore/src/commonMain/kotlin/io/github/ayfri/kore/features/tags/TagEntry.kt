package io.github.ayfri.kore.features.tags

import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = TagEntry.Companion.TagEntrySerializer::class)
data class TagEntry(
	@SerialName("id")
	val name: String,
	val required: Boolean? = null,
) {
	companion object {
		data object TagEntrySerializer : SinglePropertySimplifierSerializer<TagEntry>(generatedSerializer(), "id")
	}
}
