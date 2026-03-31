package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.load

fun helpersTests() = dataPack("helpers_tests") {
	setTestPath()
	schedulerTest()

	load {
		displayTests()
		inventoryManagerTests()
		mannequinTests()
		scoreboardTests()
	}
}.generate()
