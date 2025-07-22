package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TooltipStyleComponent.Companion.TooltipStyleComponentSerializer::class)
data class TooltipStyleComponent(var model: ModelArgument) : Component() {
	companion object {
		object TooltipStyleComponentSerializer : InlineSerializer<TooltipStyleComponent, ModelArgument>(
			ModelArgument.serializer(),
			TooltipStyleComponent::model
		)
	}
}

fun ComponentsScope.tooltipStyle(model: ModelArgument) = apply {
	this[ItemComponentTypes.TOOLTIP_STYLE] = TooltipStyleComponent(model)
}

fun ComponentsScope.tooltipStyle(model: String, namespace: String = "minecraft") = tooltipStyle(ModelArgument(model, namespace))
