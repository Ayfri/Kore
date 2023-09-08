package helpers

import arguments.chatcomponents.text
import arguments.chatcomponents.textComponent
import arguments.colors.Color
import functions.Function

fun Function.scoreboardTests() {
	ScoreboardDisplay.resetAll()

	scoreboardDisplay("test") {
		displayName = textComponent(" Cité du swagg ", Color.GOLD)

		setLine(1, "Stonks Industries", Color.LIGHT_PURPLE)
		setLine(2, textComponent(" 64 712 096 ", Color.GREEN) + text("SWAGG", Color.WHITE))
		setLine(4, "aypierre", Color.LIGHT_PURPLE)
		setLine(5, textComponent(" 12 943 666 ", Color.GREEN) + text("SWAGG", Color.WHITE))
		setLine(6, textComponent("Richest player!")) {
			createIf {
				predicate("stonks")
			}
		}
	}
}
