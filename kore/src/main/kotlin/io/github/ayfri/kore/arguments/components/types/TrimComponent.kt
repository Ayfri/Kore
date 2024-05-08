package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.TrimMaterialArgument
import io.github.ayfri.kore.arguments.types.resources.TrimPatternArgument
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrimComponent(
	var pattern: TrimPatternArgument,
	var material: TrimMaterialArgument,
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component()

fun ComponentsScope.trim(
	pattern: TrimPatternArgument,
	material: TrimMaterialArgument,
	showInTooltip: Boolean? = null,
) = apply { this[ComponentTypes.TRIM] = TrimComponent(pattern, material, showInTooltip) }
