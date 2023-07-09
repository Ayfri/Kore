package arguments.selector

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(Sort.Companion.SortSerializer::class)
enum class Sort {
	NEAREST,
	FURTHEST,
	RANDOM,
	ARBITRARY;

	companion object {
		data object SortSerializer : LowercaseSerializer<Sort>(entries)
	}
}
