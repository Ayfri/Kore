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
		is RGB -> this
		is ARGB -> RGB(red, green, blue)
		else -> error("Unknown color type: $this")
	}

	companion object {
		val AQUA = FormattingColor.AQUA
		val BLACK = FormattingColor.BLACK
		val BLUE = FormattingColor.BLUE
		val DARK_AQUA = FormattingColor.DARK_AQUA
		val DARK_BLUE = FormattingColor.DARK_BLUE
		val DARK_GRAY = FormattingColor.DARK_GRAY
		val DARK_GREEN = FormattingColor.DARK_GREEN
		val DARK_PURPLE = FormattingColor.DARK_PURPLE
		val DARK_RED = FormattingColor.DARK_RED
		val GOLD = FormattingColor.GOLD
		val GRAY = FormattingColor.GRAY
		val GREEN = FormattingColor.GREEN
		val LIGHT_PURPLE = FormattingColor.LIGHT_PURPLE
		val PINK = BossBarColor.PINK
		val PURPLE = BossBarColor.PURPLE
		val RED = FormattingColor.RED
		val WHITE = FormattingColor.WHITE
		val YELLOW = FormattingColor.YELLOW

		object ColorSerializer : KSerializer<Color> {
			override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

			override fun deserialize(decoder: Decoder) = error("Color deserialization is not supported")

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
