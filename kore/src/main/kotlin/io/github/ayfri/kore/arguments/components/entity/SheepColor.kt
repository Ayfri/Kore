package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:sheep/color` entity component, which sets the wool color of a sheep.
 *
 * Exposed on sheep spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the dye color string directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#sheep/color
 */
@Serializable(with = SheepColor.Companion.SheepColorSerializer::class)
data class SheepColor(
	var color: DyeColors
) : Component() {
	companion object {
		data object SheepColorSerializer : InlineAutoSerializer<SheepColor>(SheepColor::class)
	}
}

/** Sets the wool color of a sheep. */
fun ComponentsScope.sheepColor(color: DyeColors) {
	this[EntityItemComponentTypes.SHEEP_COLOR] = SheepColor(color)
}
