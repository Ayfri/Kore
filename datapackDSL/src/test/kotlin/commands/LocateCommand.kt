package commands

import assertions.assertsIs
import functions.Function
import generated.Biomes
import generated.ConfiguredStructures
import generated.Tags

fun Function.locateTests() {
	locateBiome(Biomes.PLAINS) assertsIs "locate biome minecraft:plains"
	locateStructure(ConfiguredStructures.MANSION) assertsIs "locate structure minecraft:mansion"
	locateStructure(Tags.Worldgen.Structure.VILLAGE) assertsIs "locate structure #minecraft:village"
	locatePointOfInterest("minecraft:village/plains/houses") assertsIs "locate poi minecraft:village/plains/houses"
}
