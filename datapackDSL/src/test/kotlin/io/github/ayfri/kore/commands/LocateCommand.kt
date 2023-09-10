package io.github.ayfri.kore.commands

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.ConfiguredStructures
import io.github.ayfri.kore.generated.Tags

fun Function.locateTests() {
	locateBiome(Biomes.PLAINS) assertsIs "locate biome minecraft:plains"
	locateStructure(ConfiguredStructures.MANSION) assertsIs "locate structure minecraft:mansion"
	locateStructure(Tags.Worldgen.Structure.VILLAGE) assertsIs "locate structure #minecraft:village"
	locatePointOfInterest("minecraft:village/plains/houses") assertsIs "locate poi minecraft:village/plains/houses"
}
