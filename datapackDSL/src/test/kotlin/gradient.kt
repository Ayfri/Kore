import arguments.Color
import arguments.allPlayers
import arguments.chatcomponents.textComponent
import arguments.toRGB
import commands.tellraw
import functions.function

fun gradient(datapack: DataPack) = datapack.function("gradient") {
	val firstColor = Color.DARK_RED.toRGB()
	val secondColor = Color.DARK_BLUE.toRGB()
	val text = "Hello world! "
	val colors = firstColor.mix(secondColor, text.length * 5)

	tellraw(allPlayers(), colors.mapIndexed { index, color ->
		textComponent {
			this.text = text[index % text.length].toString()
			this.color = color
		}
	}.reduce { acc, textComponent -> acc + textComponent })
}
