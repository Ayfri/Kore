package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AxolotlVariants.Companion.AxolotlVariantSerializer::class)
enum class AxolotlVariants {
	LUCY,
	WILD,
	GOLD,
	CYAN,
	BLUE;

	companion object {
		data object AxolotlVariantSerializer : LowercaseSerializer<AxolotlVariants>(entries)
	}
}
