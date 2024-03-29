package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data object HideAdditionalTooltipComponent : Component()

fun Components.hideAdditionalTooltip() = apply {
	this[ComponentTypes.HIDE_ADDITIONAL_TOOLTIP] = HideAdditionalTooltipComponent
}
