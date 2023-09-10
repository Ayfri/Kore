package io.github.ayfri.kore.arguments.selector

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

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
