package arguments.colors

import arguments.Color
import kotlinx.serialization.Serializable
import serializers.ToStringSerializer

@Serializable(ARGB.Companion.ARGBSerializer::class)
data class ARGB(var alpha: Int, var red: Int, var green: Int, var blue: Int) : Color {
	private constructor(hex: String) : this(
		hex.substring(0, 2).toInt(16),
		hex.substring(2, 4).toInt(16),
		hex.substring(4, 6).toInt(16),
		hex.substring(6, 8).toInt(16),
	)

	val hex get() = "%02x%02x%02x%02x".format(alpha, red, green, blue)
	val hexWithHash get() = "#$hex"

	val a get() = alpha
	val r get() = red
	val g get() = green
	val b get() = blue

	val array get() = intArrayOf(alpha, red, green, blue)
	val normalizedArray get() = doubleArrayOf(red / 255.0, green / 255.0, blue / 255.0)

	operator fun plus(other: ARGB) = ARGB(
		(alpha + other.alpha).coerceIn(0, 255),
		(red + other.red).coerceIn(0, 255),
		(green + other.green).coerceIn(0, 255),
		(blue + other.blue).coerceIn(0, 255),
	)

	operator fun minus(other: ARGB) = ARGB(
		(alpha - other.alpha).coerceIn(0, 255),
		(red - other.red).coerceIn(0, 255),
		(green - other.green).coerceIn(0, 255),
		(blue - other.blue).coerceIn(0, 255),
	)

	operator fun times(quotient: Double) = ARGB(
		(alpha * quotient).toInt().coerceIn(0, 255),
		(red * quotient).toInt().coerceIn(0, 255),
		(green * quotient).toInt().coerceIn(0, 255),
		(blue * quotient).toInt().coerceIn(0, 255),
	)

	operator fun div(quotient: Double) = ARGB(
		(alpha / quotient).toInt().coerceIn(0, 255),
		(red / quotient).toInt().coerceIn(0, 255),
		(green / quotient).toInt().coerceIn(0, 255),
		(blue / quotient).toInt().coerceIn(0, 255),
	)

	@Throws(IndexOutOfBoundsException::class)
	operator fun get(index: Int) = when (index) {
		0 -> alpha
		1 -> red
		2 -> green
		3 -> blue
		else -> throw IndexOutOfBoundsException()
	}

	@Throws(IndexOutOfBoundsException::class)
	operator fun set(index: Int, value: Int) = when (index) {
		0 -> alpha = value
		1 -> red = value
		2 -> green = value
		3 -> blue = value
		else -> throw IndexOutOfBoundsException()
	}

	fun greyscale() = ARGB(alpha, (red + green + blue) / 3, (red + green + blue) / 3, (red + green + blue) / 3)
	fun invert() = ARGB(255 - alpha, 255 - red, 255 - green, 255 - blue)

	fun mix(other: ARGB, ratio: Double) = ARGB(
		(alpha * (1 - ratio) + other.alpha * ratio).toInt().coerceIn(0, 255),
		(red * (1 - ratio) + other.red * ratio).toInt().coerceIn(0, 255),
		(green * (1 - ratio) + other.green * ratio).toInt().coerceIn(0, 255),
		(blue * (1 - ratio) + other.blue * ratio).toInt().coerceIn(0, 255),
	)

	fun toHex(withHash: Boolean = false) = if (withHash) hexWithHash else hex

	override fun toString() = hexWithHash

	companion object {
		fun fromHex(hex: String) = ARGB(hex.removePrefix("#"))
		fun fromNamedColor(color: NamedColor) = RGB.fromNamedColor(color).toARGB()

		object ARGBSerializer : ToStringSerializer<ARGB>()
	}
}
