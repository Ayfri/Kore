package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.TrimMaterialArgument
import io.github.ayfri.kore.arguments.types.resources.TrimPatternArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrimComponent(
	var pattern: TrimPatternArgument,
	var material: TrimMaterialArgument,
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component()

fun Components.trim(
	pattern: TrimPatternArgument,
	material: TrimMaterialArgument,
	showInTooltip: Boolean? = null,
) = apply { components["trim"] = TrimComponent(pattern, material, showInTooltip) }
