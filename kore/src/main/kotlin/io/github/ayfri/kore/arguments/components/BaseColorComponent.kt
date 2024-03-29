package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.generated.ComponentTypes
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

fun Components.baseColor(color: FormattingColor) = apply {
	this[ComponentTypes.BASE_COLOR] = BaseColorComponent(color)
}
