package io.github.ayfri.kore.features.worldgen.environmentattributes

import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable

/**
 * Sealed interface representing all available modifiers for Environment Attributes.
 * Modifiers control how an attribute value from one source is applied on top of a preceding value.
 */
@Serializable(with = EnvironmentAttributeModifier.Companion.EnvironmentAttributeModifierSerializer::class)
sealed interface EnvironmentAttributeModifier {
	val name get() = this::class.simpleName!!

	/** Modifiers applicable to boolean environment attributes. */
	@Serializable(with = EnvironmentAttributeModifierSerializer::class)
	sealed interface Boolean : EnvironmentAttributeModifier

	/** Modifiers applicable to color environment attributes. */
	@Serializable(with = EnvironmentAttributeModifierSerializer::class)
	sealed interface Color : EnvironmentAttributeModifier

	/** Modifiers applicable to float environment attributes. */
	@Serializable(with = EnvironmentAttributeModifierSerializer::class)
	sealed interface Float : EnvironmentAttributeModifier

	/** Replaces the preceding value entirely. Supported by all attribute types. */
	data object OVERRIDE : Boolean, Float, Color

	/** Logical AND on the preceding boolean value. */
	data object AND : Boolean

	/** Logical NAND on the preceding boolean value. */
	data object NAND : Boolean

	/** Logical OR on the preceding boolean value. */
	data object OR : Boolean

	/** Logical NOR on the preceding boolean value. */
	data object NOR : Boolean

	/** Logical XOR on the preceding boolean value. */
	data object XOR : Boolean

	/** Logical XNOR on the preceding boolean value. */
	data object XNOR : Boolean

	/** Adds the argument to the preceding float value. */
	data object ADD : Float, Color

	/** Subtracts the argument from the preceding float value. */
	data object SUBTRACT : Float, Color

	/** Multiplies the preceding float value by the argument. */
	data object MULTIPLY : Float, Color

	/** Takes the minimum of the preceding float value and the argument. */
	data object MINIMUM : Float

	/** Takes the maximum of the preceding float value and the argument. */
	data object MAXIMUM : Float

	/** Traditional alpha blending on the preceding color value. Argument format is ARGB. */
	data object ALPHA_BLEND : Color

	companion object {
		data object EnvironmentAttributeModifierSerializer : ToStringSerializer<EnvironmentAttributeModifier>({
			name.lowercase()
		})
	}
}
