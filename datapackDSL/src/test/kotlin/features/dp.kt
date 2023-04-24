package features

import configuration
import dataPack
import features.worldgen.dimensionTypeTests
import setTestPath

fun featuresTests() = dataPack("features_tests") {
	setTestPath()

	configuration {
		prettyPrint = true
		prettyPrintIndent = "\t"
	}

	advancementTests()
	dimensionTypeTests()
	predicateTests()
	recipeTest()
}.generate()
