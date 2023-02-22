package commands

import arguments.allPlayers
import arguments.textComponent
import dataPack
import functions.Function
import functions.function
import minecraftSaveTestPath


object Tellraw {
	val dp = dataPack("unit_tests") {
		path = minecraftSaveTestPath
		function("complete") { complete() }
	}

	fun launchTests() = dp.generate()

	fun Function.complete() {
		tellraw(allPlayers(), textComponent("test"))
	}
}
