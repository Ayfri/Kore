package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BaseColorComponent.Companion.BaseColorComponentSerializer::class)
data class BaseColorComponent(var color: FormattingColor) : Component() {
	companion object {
		object BaseColorComponentSerializer : InlineSerializer<BaseColorComponent, FormattingColor>(
			FormattingColor.serializer(),
			BaseColorComponent::color
		)
	}
}

fun ComponentsScope.baseColor(color: FormattingColor) = apply {
	this[ItemComponentTypes.BASE_COLOR] = BaseColorComponent(color)
}
