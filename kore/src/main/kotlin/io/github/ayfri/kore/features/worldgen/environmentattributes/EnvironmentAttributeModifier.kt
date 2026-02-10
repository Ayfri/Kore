package io.github.ayfri.kore.features.worldgen.environmentattributes

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

	/** Blends the preceding color value toward gray with the given brightness and factor. */
	data class BlendToGray(
		var brightness: kotlin.Float,
		var factor: kotlin.Float,
	) : Color {
		override val name get() = "BlendToGray"
	}

	companion object {
		data object EnvironmentAttributeModifierSerializer : KSerializer<EnvironmentAttributeModifier> {
			override val descriptor = PrimitiveSerialDescriptor("EnvironmentAttributeModifier", PrimitiveKind.STRING)

			override fun deserialize(decoder: Decoder): EnvironmentAttributeModifier =
				error("EnvironmentAttributeModifierSerializer is not meant to be deserialized")

			override fun serialize(encoder: Encoder, value: EnvironmentAttributeModifier) {
				when {
					// TODO: Find a better way to serialize these types of objects, aka enum-likes but with some configurable values
					value is BlendToGray && encoder is JsonEncoder -> encoder.encodeJsonElement(
						buildJsonObject {
							put("type", "blend_to_gray")
							put("brightness", value.brightness)
							put("factor", value.factor)
						}
					)

					else -> encoder.encodeString(value.name.lowercase())
				}
			}
		}
	}
}
