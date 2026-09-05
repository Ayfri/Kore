package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ParrotVariants.Companion.ParrotVariantSerializer::class)
enum class ParrotVariants {
	BLUE,
	GRAY,
	GREEN,
	RED_BLUE,
	YELLOW_BLUE;

	companion object {
		data object ParrotVariantSerializer : LowercaseSerializer<ParrotVariants>(entries)
	}
}
