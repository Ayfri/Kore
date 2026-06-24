package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.VillagerVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:villager/variant` entity component, which sets the biome type of a villager.
 *
 * Exposed on villager spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#villager/variant
 */
@Serializable(with = VillagerVariant.Companion.VillagerVariantSerializer::class)
data class VillagerVariant(
	var variant: VillagerVariants
) : Component() {
	companion object {
		data object VillagerVariantSerializer : InlineAutoSerializer<VillagerVariant>(VillagerVariant::class)
	}
}

/** Sets the biome type of a villager. */
fun ComponentsScope.villagerVariant(variant: VillagerVariants) {
	this[EntityItemComponentTypes.VILLAGER_VARIANT] = VillagerVariant(variant)
}
