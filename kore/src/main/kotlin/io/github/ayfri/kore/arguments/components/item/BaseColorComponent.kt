package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BaseColorComponent.Companion.BaseColorComponentSerializer::class)
data class BaseColorComponent(var color: FormattingColor) : Component() {
	companion object {
		data object BaseColorComponentSerializer : InlineAutoSerializer<BaseColorComponent>(BaseColorComponent::class)
	}
}

fun ComponentsScope.baseColor(color: FormattingColor) = apply {
	this[ItemComponentTypes.BASE_COLOR] = BaseColorComponent(color)
}
