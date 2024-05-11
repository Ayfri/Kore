package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = HorseVariants.Companion.HorseVariantSerializer::class)
enum class HorseVariants {
	BLACK,
	BROWN,
	CHESTNUT,
	CREAMY,
	DARK_BROWN,
	GRAY,
	WHITE;

	companion object {
		data object HorseVariantSerializer : LowercaseSerializer<HorseVariants>(entries)
	}
}

@Serializable
data class Horse(var variant: HorseVariants? = null) : EntityTypeSpecific()

fun Entity.horseTypeSpecific(variant: HorseVariants? = null, block: Horse.() -> Unit = {}) = apply {
	typeSpecific = Horse(variant).apply(block)
}
