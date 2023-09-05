package arguments.colors

import kotlinx.serialization.Serializable

@Serializable(with = NamedColor.Companion.NamedColorSerializer::class)
class FormattingColor internal constructor(name: String) : NamedColor(name) {
	companion object {
		val AQUA = FormattingColor("aqua")
		val BLACK = FormattingColor("black")
		val BLUE = FormattingColor("blue")
		val DARK_AQUA = FormattingColor("dark_aqua")
		val DARK_BLUE = FormattingColor("dark_blue")
		val DARK_GRAY = FormattingColor("dark_gray")
		val DARK_GREEN = FormattingColor("dark_green")
		val DARK_PURPLE = FormattingColor("dark_purple")
		val DARK_RED = FormattingColor("dark_red")
		val GOLD = FormattingColor("gold")
		val GRAY = FormattingColor("gray")
		val GREEN = FormattingColor("green")
		val LIGHT_PURPLE = FormattingColor("light_purple")
		val RED = FormattingColor("red")
		val WHITE = FormattingColor("white")
		val YELLOW = FormattingColor("yellow")
	}
}
