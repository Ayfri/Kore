package commands

import arguments.allPlayers
import arguments.chatcomponents.textComponent
import dataPack
import functions.Function
import functions.function
import setTestPath


object Tellraw {
	val dp = dataPack("unit_tests") {
		setTestPath()
		function("complete") { complete() }
	}

	fun launchTests() = dp.generate()

	fun Function.complete() {
		tellraw(allPlayers(), textComponent("test"))
	}
}
