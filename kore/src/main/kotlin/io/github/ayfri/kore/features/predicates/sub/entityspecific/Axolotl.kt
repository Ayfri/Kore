package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.features.predicates.sub.Entity
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

fun Entity.axolotlTypeSpecific(variant: AxolotlVariants? = null, block: Axolotl.() -> Unit = {}) = apply {
	typeSpecific = Axolotl(variant).apply(block)
}
