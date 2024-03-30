package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FoxVariants.Companion.FoxTypeSerializer::class)
enum class FoxVariants {
	RED,
	SNOW;

	companion object {
		data object FoxTypeSerializer : LowercaseSerializer<FoxVariants>(entries)
	}
}

@Serializable
data class Fox(var variant: FoxVariants? = null) : EntityTypeSpecific()
