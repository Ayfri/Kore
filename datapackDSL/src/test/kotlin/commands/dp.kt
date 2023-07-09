package commands

import dataPack
import functions.load

val dp = dataPack("unit_tests") {
	load {
		cloneTests()
		damageTests()
		dataTests()
		effectTests()
		executeTests()
		forceLoadTests()
		gameruleTests()
		itemTests()
		lootTests()
		particleTests()
		recipeTests()
		returnCommand()
		rideTests()
		scheduleTests()
		tellrawTests()
		titleTests()
		weatherTests()
	}
}

fun runUnitTests() = dp.generate()
