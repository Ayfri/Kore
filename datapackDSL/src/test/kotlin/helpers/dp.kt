package helpers

import dataPack
import setTestPath

fun helpersTests() = dataPack("helpers_tests") {
	setTestPath()
	schedulerTest()
}.generate()
