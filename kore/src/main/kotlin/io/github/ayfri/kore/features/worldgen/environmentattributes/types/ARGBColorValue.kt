package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.arguments.colors.ARGB
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/** Represents an ARGB color environment attribute value, used for cloud color settings. */
@Serializable(with = CloudColorValue.Companion.CloudColorValueSerializer::class)
data class CloudColorValue(
	var value: ARGB,
) : EnvironmentAttributesType() {
	companion object {
		data object CloudColorValueSerializer : InlineAutoSerializer<CloudColorValue>(CloudColorValue::class)
	}
}

/** The color of clouds, expressed as an ARGB hex string. */
fun EnvironmentAttributesScope.cloudColor(color: ARGB, modifier: EnvironmentAttributeModifier.Color? = null) = apply {
	this[EnvironmentAttributes.Visual.CLOUD_COLOR] = environmentAttributeValue(CloudColorValue(color), modifier)
}

/** The color of the sunrise/sunset, expressed as an ARGB hex string. Interpolated. */
fun EnvironmentAttributesScope.sunriseSunsetColor(color: ARGB, modifier: EnvironmentAttributeModifier.Color? = null) = apply {
	this[EnvironmentAttributes.Visual.SUNRISE_SUNSET_COLOR] = environmentAttributeValue(CloudColorValue(color), modifier)
}
