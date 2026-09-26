package io.github.ayfri.kore.arguments.colors

import io.github.ayfri.kore.arguments.Argument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Unified color abstraction used throughout Kore.
 *
 * Implementations cover named colors (chat/UI), numeric colors (RGB/ARGB), and dye colors via helpers.
 *
 * See documentation: https://kore.ayfri.com/docs/concepts/colors
 */
@Serializable(Color.Companion.ColorSerializer::class)
interface Color : Argument {
	override fun asString() = toString()

	/**
	 * Converts any color to an [RGB] instance.
	 * - [NamedColor] is mapped using vanilla-compatible tints.
	 * - [ARGB] drops the alpha component.
	 */
	fun toRGB() = when (this) {
		is NamedColor -> RGB.fromNamedColor(this)
		is RGB -> this
		is ARGB -> RGB(red, green, blue)
		else -> error("Unknown color type: $this")
	}

	/**
	 * Converts any color to an [ARGB] instance, with an optional alpha value defaulting to 255 (so assuming a non-normalized color).
	 *
	 * - [NamedColor] is mapped using vanilla-compatible tints.
	 * - [RGB] adds full alpha.
	 */
	fun toARGB(alpha: Int = 255): ARGB = when (this) {
		is NamedColor -> ARGB.fromNamedColor(this, alpha)
		is RGB -> ARGB(alpha, red, green, blue)
		is ARGB -> this
		else -> error("Unknown color type: $this")
	}

	/** Aliases are getters, not fields: initializing an enum implementing [Color] runs this companion's init mid-enum-init, freezing fields as `null`. */
	companion object {
		val AQUA get() = FormattingColor.AQUA
		val BLACK get() = FormattingColor.BLACK
		val BLUE get() = FormattingColor.BLUE
		val DARK_AQUA get() = FormattingColor.DARK_AQUA
		val DARK_BLUE get() = FormattingColor.DARK_BLUE
		val DARK_GRAY get() = FormattingColor.DARK_GRAY
		val DARK_GREEN get() = FormattingColor.DARK_GREEN
		val DARK_PURPLE get() = FormattingColor.DARK_PURPLE
		val DARK_RED get() = FormattingColor.DARK_RED
		val GOLD get() = FormattingColor.GOLD
		val GRAY get() = FormattingColor.GRAY
		val GREEN get() = FormattingColor.GREEN
		val LIGHT_PURPLE get() = FormattingColor.LIGHT_PURPLE
		val PINK get() = BossBarColor.PINK
		val PURPLE get() = BossBarColor.PURPLE
		val RED get() = FormattingColor.RED
		val WHITE get() = FormattingColor.WHITE
		val YELLOW get() = FormattingColor.YELLOW

		/**
		 * Variant serializer that emits and parses the correct format automatically:
		 * - [FormattingColor]/[BossBarColor] → lowercase name (e.g. "red").
		 * - [RGB] → string "#rrggbb".
		 * - [ARGB] → string "#aarrggbb".
		 */
		data object ColorSerializer : KSerializer<Color> {
			override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

			override fun deserialize(decoder: Decoder): Color {
				val string = decoder.decodeString()
				if (string.startsWith("#")) {
					val hex = string.removePrefix("#")
					return when (hex.length) {
						6 -> RGB.fromHex(hex)
						8 -> ARGB.fromHex(hex)
						else -> error("Invalid hex color: $string")
					}
				}

				return FormattingColor.entries.find { it.name.lowercase() == string }
					?: BossBarColor.entries.find { it.name.lowercase() == string }
					?: error("Unknown color name: $string")
			}

			override fun serialize(encoder: Encoder, value: Color) = when (value) {
				is BossBarColor -> encoder.encodeSerializableValue(BossBarColor.serializer(), value)
				is FormattingColor -> encoder.encodeSerializableValue(FormattingColor.serializer(), value)
				is RGB -> encoder.encodeSerializableValue(RGB.serializer(), value)
				is ARGB -> encoder.encodeSerializableValue(ARGB.serializer(), value)
				else -> error("Unknown color type: $value")
			}
		}
	}
}

/** Creates an [RGB] from components 0..255. */
fun color(red: Int, green: Int, blue: Int) = RGB(red, green, blue)
/** Creates an [RGB] from a hex string, accepts with or without '#'. */
fun color(hex: String) = RGB.fromHex(hex)
/** Creates an [RGB] from a decimal 0xRRGGBB. */
fun color(decimal: Int) = RGB.fromDecimal(decimal)
/** Creates an [ARGB] from components 0..255. */
fun color(red: Int, green: Int, blue: Int, alpha: Int) = ARGB(alpha, red, green, blue)

/** Alias for [color] RGB factory. */
fun rgb(red: Int, green: Int, blue: Int) = RGB(red, green, blue)
fun rgb(hex: String) = RGB.fromHex(hex)
fun rgb(decimal: Int) = RGB.fromDecimal(decimal)

/** Alias for [color] ARGB factory. */
fun argb(alpha: Int, red: Int, green: Int, blue: Int) = ARGB(alpha, red, green, blue)
fun argb(hex: String) = ARGB.fromHex(hex)

/** Weighted mix of two RGB colors. */
fun mix(color1: RGB, weight1: Double, color2: RGB, weight2: Double) = color1 * weight1 + color2 * weight2
/** Weighted mix of two ARGB colors. */
fun mix(color1: ARGB, weight1: Double, color2: ARGB, weight2: Double) = color1 * weight1 + color2 * weight2

/** Converts a [NamedColor] to [ARGB] with full alpha. */
fun NamedColor.toARGB() = ARGB.fromNamedColor(this)
