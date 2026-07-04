package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LlamaVariants.Companion.LlamaVariantSerializer::class)
enum class LlamaVariants {
	BROWN,
	CREAMY,
	GRAY,
	WHITE;

	companion object {
		data object LlamaVariantSerializer : LowercaseSerializer<LlamaVariants>(entries)
	}
}
