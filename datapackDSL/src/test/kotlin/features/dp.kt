package features

import configuration
import dataPack
import features.worldgen.*
import setTestPath

fun featuresTests() = dataPack("features_tests") {
	setTestPath()

	configuration {
		prettyPrint = true
		prettyPrintIndent = "\t"
	}

	advancementTests()
	biomeTests()
	chatTypeTests()
	damageTypeTests()
	dimensionTests()
	dimensionTypeTests()
	flatLevelGeneratorPresetTests()
	itemModifierTests()
	lootTableTests()
	noiseSettingsTests()
	predicateTests()
	recipeTest()
	worldPresetTests()
}.generate()
