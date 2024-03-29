package io.github.ayfri.kore.features.advancements.types.entityspecific

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MooshroomVariants.Companion.MooshroomVariantSerializer::class)
enum class MooshroomVariants {
	CREAMY,
	WHITE,
	BROWN,
	GRAY;

	companion object {
		data object MooshroomVariantSerializer : LowercaseSerializer<MooshroomVariants>(entries)
	}
}

@Serializable
data class Mooshroom(var variant: MooshroomVariants? = null) : EntityTypeSpecific()
