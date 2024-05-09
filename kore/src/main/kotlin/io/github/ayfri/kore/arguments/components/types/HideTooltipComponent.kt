package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data object HideTooltipComponent : Component()

fun ComponentsScope.hideTooltip() = apply { this[ComponentTypes.HIDE_TOOLTIP] = HideTooltipComponent }
