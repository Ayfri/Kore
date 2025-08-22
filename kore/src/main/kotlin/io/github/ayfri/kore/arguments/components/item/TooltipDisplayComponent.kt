package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.DataComponentTypeArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TooltipDisplayComponent(
	@SerialName("hide_tooltip")
	var hideTooltip: Boolean = false,
	@SerialName("hidden_components")
	var hiddenComponents: List<DataComponentTypeArgument> = emptyList()
) : Component()

fun ComponentsScope.tooltipDisplay(hideTooltip: Boolean = false, hiddenComponents: List<DataComponentTypeArgument>) = apply {
	this[ItemComponentTypes.TOOLTIP_DISPLAY] = TooltipDisplayComponent(hideTooltip, hiddenComponents)
}

fun ComponentsScope.tooltipDisplay(hideTooltip: Boolean = false, vararg hiddenComponents: DataComponentTypeArgument) = apply {
	this[ItemComponentTypes.TOOLTIP_DISPLAY] = TooltipDisplayComponent(hideTooltip, hiddenComponents.toList())
}
