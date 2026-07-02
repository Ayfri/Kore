package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.SalmonSizes
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:salmon/size` entity component, which sets the size of a salmon (small, medium or large).
 *
 * Exposed on salmon spawn eggs/buckets (and the entity itself) since snapshot 25w04a. Serializes as the size id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#salmon/size
 */
@Serializable(with = SalmonSize.Companion.SalmonSizeSerializer::class)
data class SalmonSize(
	var size: SalmonSizes
) : Component() {
	companion object {
		data object SalmonSizeSerializer :
			InlineAutoSerializer<SalmonSize, SalmonSizes>(serializer<SalmonSizes>(), SalmonSize::size, ::SalmonSize)
	}
}

/** Sets the size of a salmon (small, medium or large). */
fun ComponentsScope.salmonSize(size: SalmonSizes) {
	this[EntityItemComponentTypes.SALMON_SIZE] = SalmonSize(size)
}
