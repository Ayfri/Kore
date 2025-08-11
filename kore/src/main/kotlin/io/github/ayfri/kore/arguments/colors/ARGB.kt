package io.github.ayfri.kore.arguments.colors

import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable

/**
 * 32-bit color value with alpha (alpha/red/green/blue), string form "#aarrggbb".
 *
 * Encoded using [ToStringSerializer] (hex string). When alpha is not needed, prefer [RGB].
 *
 * See documentation: https://kore.ayfri.com/docs/colors
 */
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

	infix fun mix(other: ARGB) = ARGB(
		(alpha + other.alpha) / 2,
		(red + other.red) / 2,
		(green + other.green) / 2,
		(blue + other.blue) / 2,
	)

	fun mix(other: ARGB, ratio: Double) = this * (1 - ratio) + other * ratio

	fun mix(other: ARGB, count: Int): List<ARGB> {
		val step = 1.0 / count
		return List(count) { mix(other, it * step) }
	}

	fun toHex(withHash: Boolean = false) = if (withHash) hexWithHash else hex

	override fun toString() = hexWithHash

	companion object {
		/** Builds an [ARGB] from a hex string with or without leading '#'. */
		fun fromHex(hex: String) = ARGB(hex.removePrefix("#"))
		fun fromNamedColor(color: NamedColor) = RGB.fromNamedColor(color).toARGB()

		data object ARGBSerializer : ToStringSerializer<ARGB>()
	}
}
