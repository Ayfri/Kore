package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data object HideAdditionalTooltipComponent : Component()

fun ComponentsScope.hideAdditionalTooltip() = apply {
	this[ComponentTypes.HIDE_ADDITIONAL_TOOLTIP] = HideAdditionalTooltipComponent
}
