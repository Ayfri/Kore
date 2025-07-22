package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.SalmonSizes
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SalmonSize.Companion.SalmonSizeSerializer::class)
data class SalmonSize(
	var size: SalmonSizes
) : Component() {
	companion object {
		data object SalmonSizeSerializer : InlineAutoSerializer<SalmonSize>(SalmonSize::class)
	}
}

fun ComponentsScope.salmonSize(size: SalmonSizes) {
	this[EntityItemComponentTypes.SALMON_SIZE] = SalmonSize(size)
}
