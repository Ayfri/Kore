package io.github.ayfri.kore.features.predicates.sub.entityspecific

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

@Serializable
data class Rabbit(var variant: RabbitVariants? = null) : EntityTypeSpecific()
