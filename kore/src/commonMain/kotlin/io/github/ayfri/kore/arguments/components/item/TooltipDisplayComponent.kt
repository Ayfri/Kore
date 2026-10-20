package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.DataComponentTypeArgument
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:tooltip_display` item component, which controls which parts of the item tooltip are shown.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#tooltip_display
 */
@Serializable
data class TooltipDisplayComponent(
	var hideTooltip: Boolean = false,
	var hiddenComponents: List<DataComponentTypeArgument> = emptyList()
) : Component()

/** Controls which parts of the item tooltip are shown. */
fun ComponentsScope.tooltipDisplay(hideTooltip: Boolean = false, hiddenComponents: List<DataComponentTypeArgument>) = apply {
	this[ItemComponentTypes.TOOLTIP_DISPLAY] = TooltipDisplayComponent(hideTooltip, hiddenComponents)
}

fun ComponentsScope.tooltipDisplay(hideTooltip: Boolean = false, vararg hiddenComponents: DataComponentTypeArgument) = apply {
	this[ItemComponentTypes.TOOLTIP_DISPLAY] = TooltipDisplayComponent(hideTooltip, hiddenComponents.toList())
}
