package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SheepColor.Companion.SheepColorSerializer::class)
data class SheepColor(
	var color: DyeColors
) : Component() {
	companion object {
		data object SheepColorSerializer : InlineAutoSerializer<SheepColor>(SheepColor::class)
	}
}

fun ComponentsScope.sheepColor(color: DyeColors) {
	this[EntityItemComponentTypes.SHEEP_COLOR] = SheepColor(color)
}
