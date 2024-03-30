package io.github.ayfri.kore.features.predicates.sub.entityspecific

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

@Serializable
data class Parrot(var variant: ParrotVariants? = null) : EntityTypeSpecific()
