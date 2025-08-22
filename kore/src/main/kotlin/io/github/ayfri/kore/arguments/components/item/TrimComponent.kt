package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.TrimMaterialArgument
import io.github.ayfri.kore.generated.arguments.types.TrimPatternArgument
import kotlinx.serialization.Serializable

@Serializable
data class TrimComponent(
	var pattern: TrimPatternArgument,
	var material: TrimMaterialArgument,
) : Component()

fun ComponentsScope.trim(
	pattern: TrimPatternArgument,
	material: TrimMaterialArgument,
) = apply { this[ItemComponentTypes.TRIM] = TrimComponent(pattern, material) }
