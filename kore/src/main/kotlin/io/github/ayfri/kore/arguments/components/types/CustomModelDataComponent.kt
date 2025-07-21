package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDecimalSerializer
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data class CustomModelDataComponent(
	var colors: List<@Serializable(ColorAsDecimalSerializer::class) Color>? = null,
	var flags: List<Boolean>? = null,
	var floats: List<Float>? = null,
	var strings: List<String>? = null,
) : Component()

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
	customModelData(null, null, null, null, init)
