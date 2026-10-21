package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:cushion/color` entity component, which sets the dye color of a cushion.
 *
 * Serializes as the dye color string directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#cushion/color
 */
@Serializable(with = CushionColor.Companion.CushionColorSerializer::class)
data class CushionColor(
	var color: DyeColors
) : Component() {
	companion object {
		data object CushionColorSerializer :
			InlineAutoSerializer<CushionColor, DyeColors>(serializer<DyeColors>(), CushionColor::color, ::CushionColor)
	}
}

/** Sets the dye color of a cushion. */
fun ComponentsScope.cushionColor(color: DyeColors) {
	this[EntityItemComponentTypes.CUSHION_COLOR.originalName] = CushionColor(color)
}
