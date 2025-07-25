package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data object HideTooltipComponent : Component()

fun ComponentsScope.hideTooltip() = apply { this[ItemComponentTypes.HIDE_TOOLTIP] = HideTooltipComponent }
