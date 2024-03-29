package io.github.ayfri.kore.arguments.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnbreakableComponent(
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component()

fun Components.unbreakable(showInTooltip: Boolean? = null) = apply { components["unbreakable"] = UnbreakableComponent(showInTooltip) }
