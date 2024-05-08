package io.github.ayfri.kore.commands

import io.github.ayfri.kore.commands.special.testMacros
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.load

fun runUnitTests() = dataPack("unit_tests") {
	load {
		advancementTests()
		attributeTests()
		bossBarTests()
		clearTests()
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
		tickTests()
		timeTests()
		titleTests()
		transferTests()
		triggerTests()
		weatherTests()
		worldBorderTests()

		addBlankLine()
		comment("Basic commands:")
		commandsTests()
	}

	testMacros()
}.generate()
