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
	dialogTests()
	dimensionTests()
	dimensionTypeTests()
	enchantmentTests()
	enchantmentProviderTests()
	environmentAttributesTests()
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
	timelineTests()
	testEnvironmentTests()
	testInstanceTests()
	trimMaterialTests()
	trimPatternTests()
	wolfSoundVariantTests()
	wolfVariantTests()
	worldPresetTests()
}.apply {
	assertGeneratorsGenerated()
	generate()
}
