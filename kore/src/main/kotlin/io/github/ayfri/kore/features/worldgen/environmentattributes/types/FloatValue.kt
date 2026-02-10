package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/** Represents a float environment attribute value, used for numeric visual or gameplay settings. */
@Serializable(with = FloatValue.Companion.FloatValueSerializer::class)
data class FloatValue(var value: Float) : EnvironmentAttributesType() {
	companion object {
		data object FloatValueSerializer : InlineAutoSerializer<FloatValue>(FloatValue::class)
	}
}

/** The height at which all clouds appear. */
fun EnvironmentAttributesScope.cloudHeight(value: Float, mod: EnvironmentAttributeModifier.Float? = null) = apply {
	this[EnvironmentAttributes.Visual.CLOUD_HEIGHT] = environmentAttributeValue(FloatValue(value), mod)
}

/** The distance in blocks from the camera at which cloud fog ends. */
fun EnvironmentAttributesScope.cloudFogEndDistance(value: Float, mod: EnvironmentAttributeModifier.Float? = null) = apply {
	this[EnvironmentAttributes.Visual.CLOUD_FOG_END_DISTANCE] = environmentAttributeValue(FloatValue(value), mod)
}

/** The distance in blocks from the camera at which fog ends. */
fun EnvironmentAttributesScope.fogEndDistance(value: Float, mod: EnvironmentAttributeModifier.Float? = null) = apply {
	this[EnvironmentAttributes.Visual.FOG_END_DISTANCE] = environmentAttributeValue(FloatValue(value), mod)
}

/** The distance in blocks from the camera at which fog starts. */
fun EnvironmentAttributesScope.fogStartDistance(value: Float, mod: EnvironmentAttributeModifier.Float? = null) = apply {
	this[EnvironmentAttributes.Visual.FOG_START_DISTANCE] = environmentAttributeValue(FloatValue(value), mod)
}

/** The volume at which music should play. Any music playing will fade over time to this value. */
fun EnvironmentAttributesScope.musicVolume(value: Float, mod: EnvironmentAttributeModifier.Float? = null) = apply {
	this[EnvironmentAttributes.Audio.MUSIC_VOLUME] = environmentAttributeValue(FloatValue(value), mod)
}

/** The distance in blocks from the camera at which sky fog ends. */
fun EnvironmentAttributesScope.skyFogEndDistance(value: Float, mod: EnvironmentAttributeModifier.Float? = null) = apply {
	this[EnvironmentAttributes.Visual.SKY_FOG_END_DISTANCE] = environmentAttributeValue(FloatValue(value), mod)
}

/** The distance in blocks from the camera at which underwater fog ends. */
fun EnvironmentAttributesScope.waterFogEndDistance(value: Float, mod: EnvironmentAttributeModifier.Float? = null) = apply {
	this[EnvironmentAttributes.Visual.WATER_FOG_END_DISTANCE] = environmentAttributeValue(FloatValue(value), mod)
}

/** The distance in blocks from the camera at which underwater fog starts. */
fun EnvironmentAttributesScope.waterFogStartDistance(value: Float, mod: EnvironmentAttributeModifier.Float? = null) = apply {
	this[EnvironmentAttributes.Visual.WATER_FOG_START_DISTANCE] = environmentAttributeValue(FloatValue(value), mod)
}
