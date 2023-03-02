package commands

import dataPack
import functions.load
import setTestPath

val dp = dataPack("unit_tests") {
	setTestPath()
	load {
		executeTests()
		tellrawTests()
	}
}

fun runUnitTests() = dp.generate()
