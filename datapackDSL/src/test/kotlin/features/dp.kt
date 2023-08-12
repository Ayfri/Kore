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
	configuredCarverTests()
	configuredFeatureTests()
	damageTypeTests()
	densityFunctionTests()
	dimensionTests()
	dimensionTypeTests()
	flatLevelGeneratorPresetTests()
	itemModifierTests()
	lootTableTests()
	noiseTests()
	noiseSettingsTests()
	placedFeatureTests()
	predicateTests()
	processorListTests()
	recipeTest()
	structureTests()
	structureSetTests()
	worldPresetTests()
}.generate()
