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
		val values = Sort.values()
		
		object SortSerializer : LowercaseSerializer<Sort>(values)
	}
}
