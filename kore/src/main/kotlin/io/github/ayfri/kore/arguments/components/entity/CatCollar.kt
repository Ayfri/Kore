package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:cat/collar` entity component, which sets the dye color of a cat's collar.
 *
 * Exposed on cat spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the dye color string directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#cat/collar
 */
@Serializable(with = CatCollar.Companion.CatCollarSerializer::class)
data class CatCollar(
	var color: DyeColors
) : Component() {
	companion object {
		data object CatCollarSerializer : InlineAutoSerializer<CatCollar>(CatCollar::class)
	}
}

/** Sets the dye color of a cat's collar. */
fun ComponentsScope.catCollar(color: DyeColors) {
	this[EntityItemComponentTypes.CAT_COLLAR] = CatCollar(color)
}
