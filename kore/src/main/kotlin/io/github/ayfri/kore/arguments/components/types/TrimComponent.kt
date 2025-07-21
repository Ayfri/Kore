package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.TrimMaterialArgument
import io.github.ayfri.kore.generated.arguments.types.TrimPatternArgument
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
) = apply { this[ItemComponentTypes.TRIM] = TrimComponent(pattern, material, showInTooltip) }
