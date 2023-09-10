package io.github.ayfri.kore.commands

import io.github.ayfri.kore.commands.special.testMacros
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.load

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
		fillTests()
		forceLoadTests()
		functionTests()
		gameruleTests()
		itemTests()
		locateTests()
		lootTests()
		particleTests()
		placeTests()
		playSoundTests()
		randomTests()
		recipeTests()
		returnCommand()
		rideTests()
		scheduleTests()
		scoreboardTests()
		setBlockTests()
		spreadPlayersTests()
		summonTests()
		teamsTests()
		teleportTests()
		tellrawTests()
		timeTests()
		titleTests()
		triggerTests()
		weatherTests()
		worldBorderTests()

		addBlankLine()
		comment("Very simple commands:")
		commandsTests()
	}

	testMacros()
}.generate()
