package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FloatValue.Companion.FloatValueSerializer::class)
data class FloatValue(var value: Float) : EnvironmentAttributesType() {
	companion object {
		data object FloatValueSerializer : InlineAutoSerializer<FloatValue>(FloatValue::class)
	}
}

fun EnvironmentAttributesScope.cloudHeight(value: Float, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Visual.CLOUD_HEIGHT] = environmentAttributeValue(FloatValue(value), mod)
}

fun EnvironmentAttributesScope.cloudOpacity(value: Float, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Visual.CLOUD_OPACITY] = environmentAttributeValue(FloatValue(value), mod)
}

fun EnvironmentAttributesScope.musicVolume(value: Float, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Audio.MUSIC_VOLUME] = environmentAttributeValue(FloatValue(value), mod)
}

fun EnvironmentAttributesScope.waterFogRadius(value: Float, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Visual.WATER_FOG_RADIUS] = environmentAttributeValue(FloatValue(value), mod)
}
