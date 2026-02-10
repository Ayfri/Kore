package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.generated.arguments.types.MoonPhaseArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/** Represents a moon phase environment attribute value. */
@Serializable(with = MoonPhaseValue.Companion.MoonPhaseValueSerializer::class)
data class MoonPhaseValue(
	var value: MoonPhaseArgument,
) : EnvironmentAttributesType() {
	companion object {
		data object MoonPhaseValueSerializer : InlineAutoSerializer<MoonPhaseValue>(MoonPhaseValue::class)
	}
}

/** Controls the moon phase. Default is full_moon. */
fun EnvironmentAttributesScope.moonPhase(
	value: MoonPhaseArgument,
	mod: EnvironmentAttributeModifier? = null,
) = apply {
	this[EnvironmentAttributes.Visual.MOON_PHASE] = environmentAttributeValue(MoonPhaseValue(value), mod)
}
