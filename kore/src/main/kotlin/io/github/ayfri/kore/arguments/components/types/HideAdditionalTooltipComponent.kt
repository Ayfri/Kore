package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data object HideAdditionalTooltipComponent : Component()

fun ComponentsScope.hideAdditionalTooltip() = apply {
	this[ItemComponentTypes.HIDE_ADDITIONAL_TOOLTIP] = HideAdditionalTooltipComponent
}
