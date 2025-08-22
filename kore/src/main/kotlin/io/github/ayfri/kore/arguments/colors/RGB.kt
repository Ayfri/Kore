package io.github.ayfri.kore.arguments.colors

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.roundToInt

/**
 * 24-bit color value (red/green/blue), commonly serialized as a string "#rrggbb".
 *
 * Alternate serializers:
 * - [Companion.ColorAsDecimalSerializer] emits an `INT` 0xRRGGBB when Minecraft expects a decimal color.
 *
 * See documentation: https://kore.ayfri.com/docs/colors
 */
@Serializable(RGB.Companion.ColorSerializer::class)
data class RGB(var red: Int, var green: Int, var blue: Int) : Color {
	private constructor(hex: String) : this(
		hex.substring(0, 2).toInt(16),
		hex.substring(2, 4).toInt(16),
		hex.substring(4, 6).toInt(16),
	)

	val hex get() = "%02x%02x%02x".format(red, green, blue)
	val hexWithHash get() = "#$hex"

	val r get() = red
	val g get() = green
	val b get() = blue

	val array get() = intArrayOf(red, green, blue)
	val normalizedArray get() = doubleArrayOf(red / 255.0, green / 255.0, blue / 255.0)

	operator fun plus(other: RGB) = RGB(
		(red + other.red).coerceIn(0, 255),
		(green + other.green).coerceIn(0, 255),
		(blue + other.blue).coerceIn(0, 255),
	)

	operator fun minus(other: RGB) = RGB(
		(red - other.red).coerceIn(0, 255),
		(green - other.green).coerceIn(0, 255),
		(blue - other.blue).coerceIn(0, 255),
	)

	operator fun times(quotient: Double) = RGB(
		(red * quotient).roundToInt().coerceIn(0, 255),
		(green * quotient).roundToInt().coerceIn(0, 255),
		(blue * quotient).roundToInt().coerceIn(0, 255),
	)

	operator fun div(quotient: Double) = RGB(
		(red / quotient).roundToInt().coerceIn(0, 255),
		(green / quotient).roundToInt().coerceIn(0, 255),
		(blue / quotient).roundToInt().coerceIn(0, 255),
	)

	@Throws(IndexOutOfBoundsException::class)
	operator fun get(index: Int) = when (index) {
		0 -> red
		1 -> green
		2 -> blue
		else -> throw IndexOutOfBoundsException()
	}

	@Throws(IndexOutOfBoundsException::class)
	operator fun set(index: Int, value: Int) = when (index) {
		0 -> red = value
		1 -> green = value
		2 -> blue = value
		else -> throw IndexOutOfBoundsException()
	}

	fun grayscale() = RGB((red + green + blue) / 3, (red + green + blue) / 3, (red + green + blue) / 3)
	fun invert() = RGB(255 - red, 255 - green, 255 - blue)

	infix fun mix(other: RGB) = RGB(
		(red + other.red) / 2,
		(green + other.green) / 2,
		(blue + other.blue) / 2,
	)

	fun mix(other: RGB, weight: Double) = this * (1 - weight) + other * weight

	fun mix(other: RGB, count: Int): List<RGB> {
		val step = 1.0 / count
		return List(count) { mix(other, it * step) }
	}

	fun toHex(withHash: Boolean = false) = if (withHash) hexWithHash else hex

	override fun toString() = hexWithHash

	companion object {
		/** Builds an [RGB] from a 0xRRGGBB decimal integer. */
		fun fromDecimal(decimal: Int): RGB {
			val red = decimal shr 16 and 0xFF
			val green = decimal shr 8 and 0xFF
			val blue = decimal and 0xFF

			return RGB(red, green, blue)
		}

		fun fromDoubleArray(array: DoubleArray) = RGB(
			(array[0] * 255).roundToInt().coerceIn(0, 255),
			(array[1] * 255).roundToInt().coerceIn(0, 255),
			(array[2] * 255).roundToInt().coerceIn(0, 255),
		)

		/** Builds an [RGB] from a hex string with or without leading '#'. */
		fun fromHex(hex: String) = RGB(hex.removePrefix("#"))

		fun fromNamedColor(color: NamedColor) = when (color) {
			FormattingColor.AQUA -> RGB(85, 255, 255)
			FormattingColor.BLACK -> RGB(0, 0, 0)
			FormattingColor.BLUE, BossBarColor.BLUE -> RGB(85, 85, 255)
			FormattingColor.DARK_AQUA -> RGB(0, 170, 170)
			FormattingColor.DARK_BLUE -> RGB(0, 0, 170)
			FormattingColor.DARK_GRAY -> RGB(85, 85, 85)
			FormattingColor.DARK_GREEN -> RGB(0, 170, 0)
			FormattingColor.DARK_PURPLE -> RGB(170, 0, 170)
			FormattingColor.DARK_RED -> RGB(170, 0, 0)
			FormattingColor.GOLD -> RGB(255, 170, 0)
			FormattingColor.GRAY -> RGB(170, 170, 170)
			FormattingColor.GREEN, BossBarColor.GREEN -> RGB(85, 255, 85)
			FormattingColor.LIGHT_PURPLE -> RGB(255, 85, 255)
			BossBarColor.PINK -> RGB(255, 0, 255)
			BossBarColor.PURPLE -> RGB(170, 0, 255)
			FormattingColor.RED, BossBarColor.RED -> RGB(255, 85, 85)
			FormattingColor.WHITE, BossBarColor.WHITE -> RGB(255, 255, 255)
			FormattingColor.YELLOW, BossBarColor.YELLOW -> RGB(255, 255, 85)
		}

		/** Serializer that encodes to a string "#rrggbb". */
		data object ColorSerializer : KSerializer<RGB> {
			override val descriptor = PrimitiveSerialDescriptor("RGB", PrimitiveKind.STRING)
			override fun serialize(encoder: Encoder, value: RGB) = encoder.encodeString(value.hexWithHash)
			override fun deserialize(decoder: Decoder) = fromHex(decoder.decodeString())
		}

		/** Serializer that encodes to an INT 0xRRGGBB (used in components, particles, worldgen, etc.). */
		data object ColorAsDecimalSerializer : KSerializer<RGB> {
			override val descriptor = PrimitiveSerialDescriptor("RGB", PrimitiveKind.INT)
			override fun serialize(encoder: Encoder, value: RGB) = encoder.encodeInt(value.red shl 16 or (value.green shl 8) or value.blue)
			override fun deserialize(decoder: Decoder) = fromDecimal(decoder.decodeInt())
		}
	}
}
