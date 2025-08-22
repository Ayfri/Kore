package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.TrimMaterialArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ProvidesTrimMaterial.Companion.ProvidesTrimMaterialSerializer::class)
data class ProvidesTrimMaterial(var material: TrimMaterialArgument) : Component() {
	companion object {
		data object ProvidesTrimMaterialSerializer : InlineAutoSerializer<ProvidesTrimMaterial>(ProvidesTrimMaterial::class)
	}
}

fun ComponentsScope.providesTrimMaterial(material: TrimMaterialArgument) = apply {
	this[ItemComponentTypes.PROVIDES_TRIM_MATERIAL] = ProvidesTrimMaterial(material)
}
