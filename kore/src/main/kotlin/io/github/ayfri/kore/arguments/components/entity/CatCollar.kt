package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = CatCollar.Companion.CatCollarSerializer::class)
data class CatCollar(
	var color: DyeColors
) : Component() {
	companion object {
		data object CatCollarSerializer : InlineAutoSerializer<CatCollar>(CatCollar::class)
	}
}

fun ComponentsScope.catCollar(color: DyeColors) {
	this[EntityItemComponentTypes.CAT_COLLAR] = CatCollar(color)
}
