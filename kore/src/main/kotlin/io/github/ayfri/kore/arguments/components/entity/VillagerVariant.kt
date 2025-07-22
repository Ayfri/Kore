package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.VillagerVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = VillagerVariant.Companion.VillagerVariantSerializer::class)
data class VillagerVariant(
	var variant: VillagerVariants
) : Component() {
	companion object {
		data object VillagerVariantSerializer : InlineAutoSerializer<VillagerVariant>(VillagerVariant::class)
	}
}

fun ComponentsScope.villagerVariant(variant: VillagerVariants) {
	this[EntityItemComponentTypes.VILLAGER_VARIANT] = VillagerVariant(variant)
}
