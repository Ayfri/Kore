package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MooshroomVariants.Companion.MooshroomVariantSerializer::class)
enum class MooshroomVariants {
	BROWN,
	RED;

	companion object {
		data object MooshroomVariantSerializer : LowercaseSerializer<MooshroomVariants>(entries)
	}
}
