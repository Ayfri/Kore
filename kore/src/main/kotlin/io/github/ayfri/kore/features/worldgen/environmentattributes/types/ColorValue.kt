package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDecimalSerializer
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/** Represents an RGB color environment attribute value, used for visual color settings. */
@Serializable(with = ColorValue.Companion.ColorValueSerializer::class)
data class ColorValue(
	@Serializable(with = ColorAsDecimalSerializer::class)
	var value: Color,
) : EnvironmentAttributesType() {
	companion object {
		data object ColorValueSerializer : InlineAutoSerializer<ColorValue>(ColorValue::class)
	}
}

/** The color of fog when the camera is not submerged. Also affected by time of day, weather, and potion effects. */
fun EnvironmentAttributesScope.fogColor(color: Color, mod: EnvironmentAttributeModifier.Color? = null) = apply {
	this[EnvironmentAttributes.Visual.FOG_COLOR] = environmentAttributeValue(ColorValue(color), mod)
}

/** The color of the sky, only visible for the overworld sky. Also affected by time of day and weather. */
fun EnvironmentAttributesScope.skyColor(color: Color, mod: EnvironmentAttributeModifier.Color? = null) = apply {
	this[EnvironmentAttributes.Visual.SKY_COLOR] = environmentAttributeValue(ColorValue(color), mod)
}

/** The color of sky light. Interpolated. */
fun EnvironmentAttributesScope.skyLightColor(color: Color, mod: EnvironmentAttributeModifier.Color? = null) = apply {
	this[EnvironmentAttributes.Visual.SKY_LIGHT_COLOR] = environmentAttributeValue(ColorValue(color), mod)
}

/** The color of fog when submerged in water. Also affected by time of day, weather, and potion effects. */
fun EnvironmentAttributesScope.waterFogColor(color: Color, mod: EnvironmentAttributeModifier.Color? = null) = apply {
	this[EnvironmentAttributes.Visual.WATER_FOG_COLOR] = environmentAttributeValue(ColorValue(color), mod)
}
