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
	chatTypeTests()
	configuredCarverTests()
	configuredFeatureTests()
	damageTypeTests()
	densityFunctionTests()
	dimensionTests()
	dimensionTypeTests()
	enchantmentTests()
	enchantmentProviderTests()
	flatLevelGeneratorPresetTests()
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
	wolfVariantTests()
	worldPresetTests()
}.apply {
	assertGeneratorsGenerated()
	generate()
}
