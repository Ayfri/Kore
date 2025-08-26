package io.github.ayfri.kore.features

import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.features.worldgen.*
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack

fun featuresTests() = testDataPack("features_tests") {
	pretty()

	advancementTests()
	bannerPatternTests()
	biomeTests()
	catVariantTests()
	chatTypeTests()
	chickenVariantTests()
	configuredCarverTests()
	configuredFeatureTests()
	cowVariantTests()
	damageTypeTests()
	densityFunctionTests()
	dimensionTests()
	dimensionTypeTests()
	enchantmentTests()
	enchantmentProviderTests()
	flatLevelGeneratorPresetTests()
	frogVariantTests()
	itemModifierTests()
	jukeboxSongTests()
	lootTableTests()
	noiseTests()
	noiseSettingsTests()
	paintingVariantTests()
	pigVariantTests()
	placedFeatureTests()
	predicateTests()
	predicateEntityTypeSpecificTests()
	processorListTests()
	recipeTest()
	structureTests()
	structureSetTests()
	tagTests()
	templatePoolTests()
	testEnvironmentTests()
	testInstanceTests()
	trimMaterialTests()
	trimPatternTests()
	wolfVariantTests()
	worldPresetTests()
}.apply {
	assertGeneratorsGenerated()
	generate()
}
