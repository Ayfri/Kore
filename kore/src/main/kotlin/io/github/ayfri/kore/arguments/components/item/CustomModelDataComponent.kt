package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDecimalSerializer
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:custom_model_data` item component, which provides values for custom item model selection in resource packs.
 *
 * Holds parallel lists of colors, flags, floats and strings the resource pack model can read.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#custom_model_data
 */
@Serializable
data class CustomModelDataComponent(
	var colors: List<@Serializable(ColorAsDecimalSerializer::class) Color>? = null,
	var flags: List<Boolean>? = null,
	var floats: List<Float>? = null,
	var strings: List<String>? = null,
) : Component()

/** Provides numeric values for custom item model selection in resource packs. */
fun ComponentsScope.customModelData(
	colors: List<Color>? = null,
	flags: List<Boolean>? = null,
	floats: List<Float>? = null,
	strings: List<String>? = null,
	init: CustomModelDataComponent.() -> Unit = {},
) = apply {
	this[ItemComponentTypes.CUSTOM_MODEL_DATA] = CustomModelDataComponent(
		colors,
		flags,
		floats,
		strings
	).apply(init)
}

fun ComponentsScope.customModelData(init: CustomModelDataComponent.() -> Unit = {}) =
	customModelData(null, floats = init)
