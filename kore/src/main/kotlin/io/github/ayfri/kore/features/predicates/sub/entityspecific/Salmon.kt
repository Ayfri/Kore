package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SalmonVariants.Companion.SalmonVariantSerializer::class)
enum class SalmonVariants {
	SMALL,
	MEDIUM,
	LARGE;

	companion object {
		data object SalmonVariantSerializer : LowercaseSerializer<SalmonVariants>(entries)
	}
}

@Serializable
data class Salmon(var variant: SalmonVariants? = null) : EntityTypeSpecific()

fun Entity.salmonTypeSpecific(variant: SalmonVariants? = null) = apply {
	typeSpecific = Salmon(variant)
}
