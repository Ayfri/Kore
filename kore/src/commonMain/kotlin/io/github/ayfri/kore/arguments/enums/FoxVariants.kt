package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FoxVariants.Companion.FoxVariantSerializer::class)
enum class FoxVariants {
	RED,
	SNOW;

	companion object {
		data object FoxVariantSerializer : LowercaseSerializer<FoxVariants>(entries)
	}
}
