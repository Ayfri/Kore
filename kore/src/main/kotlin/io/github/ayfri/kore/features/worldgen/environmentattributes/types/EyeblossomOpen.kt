package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/** Possible values for the eyeblossom_open environment attribute. */
@Serializable(with = EyeblossomOpenState.Companion.EyeblossomOpenStateSerializer::class)
enum class EyeblossomOpenState {
	TRUE,
	FALSE,
	DEFAULT;

	companion object {
		data object EyeblossomOpenStateSerializer : LowercaseSerializer<EyeblossomOpenState>(entries)
	}
}

/** Represents an eyeblossom open environment attribute value. */
@Serializable(with = EyeblossomOpenValue.Companion.EyeblossomOpenValueSerializer::class)
data class EyeblossomOpenValue(var value: EyeblossomOpenState) : EnvironmentAttributesType() {
	companion object {
		data object EyeblossomOpenValueSerializer : InlineAutoSerializer<EyeblossomOpenValue>(EyeblossomOpenValue::class)
	}
}

/** Controls whether Eyeblossoms are open. */
fun EnvironmentAttributesScope.eyeblossomOpen(
	value: EyeblossomOpenState,
	mod: EnvironmentAttributeModifier? = null,
) = apply {
	this[EnvironmentAttributes.Gameplay.EYEBLOSSOM_OPEN] = environmentAttributeValue(EyeblossomOpenValue(value), mod)
}
