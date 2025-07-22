package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnbreakableComponent(
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component()

fun ComponentsScope.unbreakable(showInTooltip: Boolean? = null) =
	apply { this[ItemComponentTypes.UNBREAKABLE] = UnbreakableComponent(showInTooltip) }
