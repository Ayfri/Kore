package io.github.ayfri.kore.features.tags

import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = TagEntry.Companion.TagEntrySerializer::class)
data class TagEntry(
	@SerialName("id")
	val name: String,
	val required: Boolean? = null,
) {
	companion object {
		data object TagEntrySerializer : SinglePropertySimplifierSerializer<TagEntry, String>(
			TagEntry::class,
			TagEntry::name,
		)
	}
}
