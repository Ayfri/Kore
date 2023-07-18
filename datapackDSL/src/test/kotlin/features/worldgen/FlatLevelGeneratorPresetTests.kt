package features.worldgen

import DataPack
import features.worldgen.dimension.generator.Layer
import features.worldgen.flatlevelgeneratorpreset.flatLevelGeneratorPreset
import features.worldgen.flatlevelgeneratorpreset.settings
import generated.Biomes
import generated.Blocks
import generated.Items
import generated.StructureSets
import utils.assertsIs

fun DataPack.flatLevelGeneratorPresetTests() {
	flatLevelGeneratorPreset("my_flat_level_generator_preset") {
		display = Items.DIAMOND
		settings {
			biome = Biomes.PLAINS
			features = true
			lakes = false
			layers = listOf(
				Layer(Blocks.COBBLESTONE, 2),
				Layer(Blocks.DIRT, 3),
				Layer(Blocks.GRASS_BLOCK, 1),
			)
			structureOverrides = listOf(StructureSets.VILLAGES, StructureSets.STRONGHOLDS)
		}
	}

	flatLevelGeneratorPresets.last() assertsIs """
		{
			"display": "minecraft:diamond",
			"settings": {
				"biome": "minecraft:plains",
				"lakes": false,
				"features": true,
				"layers": [
					{
						"block": "minecraft:cobblestone",
						"height": 2
					},
					{
						"block": "minecraft:dirt",
						"height": 3
					},
					{
						"block": "minecraft:grass_block",
						"height": 1
					}
				],
				"structure_overrides": [
					"minecraft:villages",
					"minecraft:strongholds"
				]
			}
		}
	""".trimIndent()
}
