package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDecimalSerializer
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ColorValue.Companion.ColorValueSerializer::class)
data class ColorValue(
	@Serializable(with = ColorAsDecimalSerializer::class)
	var value: Color,
) : EnvironmentAttributesType() {
	companion object {
		data object ColorValueSerializer : InlineAutoSerializer<ColorValue>(ColorValue::class)
	}
}

fun EnvironmentAttributesScope.fogColor(color: Color, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Visual.FOG_COLOR] = environmentAttributeValue(ColorValue(color), mod)
}

fun EnvironmentAttributesScope.skyColor(color: Color, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Visual.SKY_COLOR] = environmentAttributeValue(ColorValue(color), mod)
}

fun EnvironmentAttributesScope.waterFogColor(color: Color, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Visual.WATER_FOG_COLOR] = environmentAttributeValue(ColorValue(color), mod)
}
