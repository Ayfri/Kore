package features

import features.worldgen.*
import utils.assertGeneratorsGenerated
import utils.pretty
import utils.testDataPack

fun featuresTests() = testDataPack("features_tests") {
	pretty()

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
	templatePoolTests()
	worldPresetTests()
}.apply {
	assertGeneratorsGenerated()
	generate()
}
