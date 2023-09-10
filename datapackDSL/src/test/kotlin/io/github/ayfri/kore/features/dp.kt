package io.github.ayfri.kore.features

import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.features.worldgen.*
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack

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
	tagTests()
	templatePoolTests()
	worldPresetTests()
}.apply {
	assertGeneratorsGenerated()
	generate()
}
