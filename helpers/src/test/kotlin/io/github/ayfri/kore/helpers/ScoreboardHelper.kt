package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.kotest.core.spec.style.FunSpec

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

		hideValues()
	}
}

class ScoreboardHelperTests : FunSpec({
	test("scoreboard") {
		dataPack("helpers_tests") {
			path = kotlinx.io.files.Path("out")
			load { scoreboardTests() }
		}.generate()
	}
})
