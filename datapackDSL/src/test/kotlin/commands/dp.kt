package commands

import commands.special.testMacros
import dataPack
import functions.load

fun runUnitTests() = dataPack("unit_tests") {
	load {
		advancementTests()
		attributeTests()
		bossBarTests()
		cloneTests()
		damageTests()
		dataTests()
		effectTests()
		executeTests()
		forceLoadTests()
		functionTests()
		gameruleTests()
		itemTests()
		locateTests()
		lootTests()
		particleTests()
		placeTests()
		randomTests()
		recipeTests()
		returnCommand()
		rideTests()
		scheduleTests()
		tellrawTests()
		titleTests()
		timeTests()
		weatherTests()
	}

	testMacros()
}.generate()
