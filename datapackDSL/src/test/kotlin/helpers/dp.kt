package helpers

import dataPack
import functions.load
import setTestPath

fun helpersTests() = dataPack("helpers_tests") {
	setTestPath()
	schedulerTest()

	load {
		displayTests()
		scoreboardTests()
	}
}.generate()
