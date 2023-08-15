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
		dataPackTests()
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
		scoreboardTests()
		teamsTests()
		tellrawTests()
		titleTests()
		timeTests()
		triggerTests()
		weatherTests()
		worldBorderTests()
	}

	testMacros()
}.generate()
