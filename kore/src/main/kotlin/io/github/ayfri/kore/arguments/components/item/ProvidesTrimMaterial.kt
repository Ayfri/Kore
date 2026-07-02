package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.TrimMaterialArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:provides_trim_material` item component, which registers this item as an armor trim material for the smithing table.
 *
 * Serializes as the trim material id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#provides_trim_material
 */
@Serializable(with = ProvidesTrimMaterial.Companion.ProvidesTrimMaterialSerializer::class)
data class ProvidesTrimMaterial(var material: TrimMaterialArgument) : Component() {
	companion object {
		data object ProvidesTrimMaterialSerializer : InlineAutoSerializer<ProvidesTrimMaterial, TrimMaterialArgument>(
			serializer<TrimMaterialArgument>(),
			ProvidesTrimMaterial::material,
			::ProvidesTrimMaterial
		)
	}
}

/** Registers this item as an armor trim material for the smithing table. */
fun ComponentsScope.providesTrimMaterial(material: TrimMaterialArgument) = apply {
	this[ItemComponentTypes.PROVIDES_TRIM_MATERIAL] = ProvidesTrimMaterial(material)
}
