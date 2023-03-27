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
		lootTests()
		rideTests()
		scheduleTests()
		tellrawTests()
		titleTests()
		weatherTests()
	}
}

fun runUnitTests() = dp.generate()
