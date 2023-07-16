package arguments.colors

import arguments.Argument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(Color.Companion.ColorSerializer::class)
interface Color : Argument {
	override fun asString() = toString()

	fun toRGB() = when (this) {
		is NamedColor -> RGB.fromNamedColor(this)
		is BossBarColor -> RGB.fromNamedColor(this)
		is RGB -> this
		is ARGB -> RGB(red, green, blue)
		else -> error("Unknown color type: $this")
	}

	companion object {
		val AQUA = NamedColor("aqua")
		val BLACK = NamedColor("black")
		val BLUE = BossBarColor("blue")
		val DARK_AQUA = NamedColor("dark_aqua")
		val DARK_BLUE = NamedColor("dark_blue")
		val DARK_GRAY = NamedColor("dark_gray")
		val DARK_GREEN = NamedColor("dark_green")
		val DARK_PURPLE = NamedColor("dark_purple")
		val DARK_RED = NamedColor("dark_red")
		val GOLD = NamedColor("gold")
		val GRAY = NamedColor("gray")
		val GREEN = BossBarColor("green")
		val LIGHT_PURPLE = NamedColor("light_purple")
		val PINK = BossBarColor("pink")
		val PURPLE = BossBarColor("purple")
		val RED = BossBarColor("red")
		val WHITE = BossBarColor("white")
		val YELLOW = BossBarColor("yellow")

		object ColorSerializer : KSerializer<Color> {
			override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

			override fun deserialize(decoder: Decoder) = NamedColor(decoder.decodeString())

			override fun serialize(encoder: Encoder, value: Color) = when (value) {
				is BossBarColor -> BossBarColor.serializer().serialize(encoder, value)
				is NamedColor -> NamedColor.serializer().serialize(encoder, value)
				is RGB -> RGB.serializer().serialize(encoder, value)
				is ARGB -> ARGB.serializer().serialize(encoder, value)
				else -> error("Unknown color type: $value")
			}
		}

	}

}

fun color(red: Int, green: Int, blue: Int) = RGB(red, green, blue)
fun color(hex: String) = RGB.fromHex(hex)
fun color(decimal: Int) = RGB.fromDecimal(decimal)
fun color(red: Int, green: Int, blue: Int, alpha: Int) = ARGB(alpha, red, green, blue)

fun rgb(red: Int, green: Int, blue: Int) = RGB(red, green, blue)
fun rgb(hex: String) = RGB.fromHex(hex)
fun rgb(decimal: Int) = RGB.fromDecimal(decimal)

fun argb(alpha: Int, red: Int, green: Int, blue: Int) = ARGB(alpha, red, green, blue)
fun argb(hex: String) = ARGB.fromHex(hex)

fun mix(color1: RGB, weight1: Double, color2: RGB, weight2: Double) = color1 * weight1 + color2 * weight2
fun mix(color1: ARGB, weight1: Double, color2: ARGB, weight2: Double) = color1 * weight1 + color2 * weight2

fun NamedColor.toARGB() = ARGB.fromNamedColor(this)
