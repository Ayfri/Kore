import arguments.chatcomponents.text
import arguments.chatcomponents.textComponent
import arguments.colors.Color
import arguments.types.literals.allPlayers
import commands.tellraw
import functions.function

fun DataPack.gradient() = function("gradient") {
	val firstColor = Color.DARK_RED.toRGB()
	val secondColor = Color.DARK_BLUE.toRGB()
	val text = "Hello world! "
	val colors = firstColor.mix(secondColor, text.length * 5)

	tellraw(allPlayers(), colors.mapIndexed { index, color ->
		text(text[index % text.length].toString(), color)
	}.fold(textComponent()) { acc, textComponent -> acc + textComponent })
}
