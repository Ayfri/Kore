package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.TrimMaterialArgument
import io.github.ayfri.kore.generated.arguments.types.TrimPatternArgument
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:trim` item component, which applies an armor trim pattern and material to a piece of armor.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#trim
 */
@Serializable
data class TrimComponent(
	var pattern: TrimPatternArgument,
	var material: TrimMaterialArgument,
) : Component()

/** Applies an armor trim pattern and material to a piece of armor. */
fun ComponentsScope.trim(
	pattern: TrimPatternArgument,
	material: TrimMaterialArgument,
) = apply { this[ItemComponentTypes.TRIM] = TrimComponent(pattern, material) }
