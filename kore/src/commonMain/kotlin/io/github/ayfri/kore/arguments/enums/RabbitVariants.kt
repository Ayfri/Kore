package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RabbitVariants.Companion.RabbitVariantSerializer::class)
enum class RabbitVariants {
	BLACK,
	BROWN,
	EVIL,
	GOLD,
	SALT,
	WHITE,
	WHITE_SPLOTCHED;

	companion object {
		data object RabbitVariantSerializer : LowercaseSerializer<RabbitVariants>(entries)
	}
}
