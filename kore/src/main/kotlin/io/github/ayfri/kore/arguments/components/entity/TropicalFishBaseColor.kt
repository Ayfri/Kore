package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TropicalFishBaseColor.Companion.TropicalFishBaseColorSerializer::class)
data class TropicalFishBaseColor(
	var color: DyeColors
) : Component() {
	companion object {
		data object TropicalFishBaseColorSerializer : InlineAutoSerializer<TropicalFishBaseColor>(TropicalFishBaseColor::class)
	}
}

fun ComponentsScope.tropicalFishBaseColor(color: DyeColors) {
	this[EntityItemComponentTypes.TROPICAL_FISH_BASE_COLOR] = TropicalFishBaseColor(color)
}
