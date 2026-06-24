package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:tooltip_style` item component, which sets a custom resource-pack model for the item tooltip background.
 *
 * Serializes as the model id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#tooltip_style
 */
@Serializable(with = TooltipStyleComponent.Companion.TooltipStyleComponentSerializer::class)
data class TooltipStyleComponent(var model: ModelArgument) : Component() {
	companion object {
		data object TooltipStyleComponentSerializer : InlineAutoSerializer<TooltipStyleComponent>(TooltipStyleComponent::class)
	}
}

/** Sets a custom resource-pack model for the item tooltip background. */
fun ComponentsScope.tooltipStyle(model: ModelArgument) = apply {
	this[ItemComponentTypes.TOOLTIP_STYLE] = TooltipStyleComponent(model)
}

fun ComponentsScope.tooltipStyle(model: String, namespace: String = "minecraft") = tooltipStyle(ModelArgument(model, namespace))
