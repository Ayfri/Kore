package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BoatVariants.Companion.BoatVariantSerializer::class)
enum class BoatVariants {
	ACACIA,
	BAMBOO,
	BIRCH,
	CHERRY,
	DARK_OAK,
	JUNGLE,
	MANGROVE,
	OAK,
	SPRUCE;

	companion object {
		data object BoatVariantSerializer : LowercaseSerializer<BoatVariants>(
			entries
		)
	}
}

@Serializable
data class Boat(var variant: BoatVariants? = null) : EntityTypeSpecific()

fun Entity.boatTypeSpecific(variant: BoatVariants? = null, block: Boat.() -> Unit = {}) = apply {
	typeSpecific = Boat(variant).apply(block)
}
