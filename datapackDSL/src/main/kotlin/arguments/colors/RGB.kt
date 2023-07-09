package arguments.colors

import arguments.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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
		(red * quotient).toInt().coerceIn(0, 255),
		(green * quotient).toInt().coerceIn(0, 255),
		(blue * quotient).toInt().coerceIn(0, 255),
	)

	operator fun div(quotient: Double) = RGB(
		(red / quotient).toInt().coerceIn(0, 255),
		(green / quotient).toInt().coerceIn(0, 255),
		(blue / quotient).toInt().coerceIn(0, 255),
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

	fun mix(other: RGB, count: Int): List<RGB> {
		val result = mutableListOf<RGB>()
		val step = 1.0 / count
		for (i in 0 until count) {
			result.add(this * (1 - step * i) + other * (step * i))
		}
		return result
	}

	fun toARGB(alpha: Int = 255) = ARGB(alpha, red, green, blue)
	fun toHex(withHash: Boolean = false) = if (withHash) hexWithHash else hex

	override fun toString() = hexWithHash

	companion object {
		fun fromHex(hex: String) = RGB(hex.removePrefix("#"))
		fun fromNamedColor(color: NamedColor) = when (color) {
			Color.AQUA -> RGB(85, 255, 255)
			Color.BLACK -> RGB(0, 0, 0)
			Color.BLUE -> RGB(85, 85, 255)
			Color.DARK_AQUA -> RGB(0, 170, 170)
			Color.DARK_BLUE -> RGB(0, 0, 170)
			Color.DARK_GRAY -> RGB(85, 85, 85)
			Color.DARK_GREEN -> RGB(0, 170, 0)
			Color.DARK_PURPLE -> RGB(170, 0, 170)
			Color.DARK_RED -> RGB(170, 0, 0)
			Color.GOLD -> RGB(255, 170, 0)
			Color.GRAY -> RGB(170, 170, 170)
			Color.GREEN -> RGB(85, 255, 85)
			Color.LIGHT_PURPLE -> RGB(255, 85, 255)
			Color.PINK -> RGB(255, 0, 255)
			Color.PURPLE -> RGB(170, 0, 255)
			Color.RED -> RGB(255, 85, 85)
			Color.WHITE -> RGB(255, 255, 255)
			Color.YELLOW -> RGB(255, 255, 85)
			else -> RGB(0, 0, 0)
		}

		object ColorSerializer : KSerializer<RGB> {
			override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("RGB", PrimitiveKind.STRING)

			override fun serialize(encoder: Encoder, value: RGB) {
				encoder.encodeString(value.hexWithHash)
			}

			override fun deserialize(decoder: Decoder) = fromHex(decoder.decodeString())
		}
	}
}
