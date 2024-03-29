package io.github.ayfri.kore.features.advancements.types.entityspecific

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

@Serializable
data class Axolotl(var variant: AxolotlVariants? = null) : EntityTypeSpecific()
