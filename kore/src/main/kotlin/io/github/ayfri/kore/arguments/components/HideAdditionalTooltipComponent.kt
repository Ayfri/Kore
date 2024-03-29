package io.github.ayfri.kore.arguments.components

import kotlinx.serialization.Serializable

@Serializable
data object HideAdditionalTooltipComponent : Component()

fun Components.hideAdditionalTooltip() = apply {
	components["hide_additional_tooltip"] = HideAdditionalTooltipComponent
}
