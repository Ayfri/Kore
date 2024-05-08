package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnbreakableComponent(
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component()

fun ComponentsScope.unbreakable(showInTooltip: Boolean? = null) =
	apply { this[ComponentTypes.UNBREAKABLE] = UnbreakableComponent(showInTooltip) }
