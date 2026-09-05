package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:base_color` item component, which sets the base color of a banner before patterns are applied.
 *
 * Serializes as the color string directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#base_color
 */
@Serializable(with = BaseColorComponent.Companion.BaseColorComponentSerializer::class)
data class BaseColorComponent(var color: FormattingColor) : Component() {
	companion object {
		data object BaseColorComponentSerializer : InlineAutoSerializer<BaseColorComponent, FormattingColor>(
			serializer<FormattingColor>(),
			BaseColorComponent::color,
			::BaseColorComponent
		)
	}
}

/** Sets the base color of a banner before patterns are applied. */
fun ComponentsScope.baseColor(color: FormattingColor) = apply {
	this[ItemComponentTypes.BASE_COLOR] = BaseColorComponent(color)
}
