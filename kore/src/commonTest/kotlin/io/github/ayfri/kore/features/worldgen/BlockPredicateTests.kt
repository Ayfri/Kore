package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.features.worldgen.blockpredicate.*
import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.blockPredicateFilter
import io.github.ayfri.kore.features.worldgen.placedfeature.placedFeature
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.ConfiguredFeatures
import io.github.ayfri.kore.generated.Fluids
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

private fun DataPack.assertPredicate(name: String, expected: String, block: BlockPredicatesScope.() -> Unit) {
	placedFeature(name, ConfiguredFeatures.ACACIA) {
		blockPredicateFilter {
			predicate = blockPredicate(block)
		}
	}

	val template = """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:block_predicate_filter",
					"predicate": <predicate>
				}
			]
		}
	""".trimIndent()

	placedFeatures.last() assertsIs template.replace("<predicate>", expected.prependIndent("\t\t\t").trimStart())
}

fun DataPack.blockPredicateTests() {
	assertPredicate(
		"all_of", """
			{
				"type": "minecraft:all_of",
				"predicates": [
					{
						"type": "minecraft:solid"
					},
					{
						"type": "minecraft:replaceable"
					}
				]
			}
		""".trimIndent()
	) {
		allOf {
			solid()
			replaceable()
		}
	}

	assertPredicate(
		"any_of", """
			{
				"type": "minecraft:any_of",
				"predicates": [
					{
						"type": "minecraft:matching_blocks",
						"blocks": "minecraft:stone"
					},
					{
						"type": "minecraft:matching_blocks",
						"blocks": "minecraft:deepslate"
					}
				]
			}
		""".trimIndent()
	) {
		anyOf {
			matchingBlocks(Blocks.STONE)
			matchingBlocks(Blocks.DEEPSLATE)
		}
	}

	assertPredicate(
		"has_sturdy_face", """
			{
				"type": "minecraft:has_sturdy_face",
				"offset": [
					0,
					-1,
					0
				],
				"direction": "up"
			}
		""".trimIndent()
	) {
		hasSturdyFace(Direction.UP) { offset(0, -1, 0) }
	}

	assertPredicate(
		"inside_world_bounds", """
			{
				"type": "minecraft:inside_world_bounds",
				"offset": [
					0,
					-3,
					0
				]
			}
		""".trimIndent()
	) {
		insideWorldBounds { offset(0, -3, 0) }
	}

	assertPredicate(
		"matching_biomes", """
			{
				"type": "minecraft:matching_biomes",
				"biomes": [
					"minecraft:plains",
					"minecraft:savanna"
				]
			}
		""".trimIndent()
	) {
		matchingBiomes(Biomes.PLAINS, Biomes.SAVANNA)
	}

	assertPredicate(
		"matching_biome_tag", """
			{
				"type": "minecraft:matching_biomes",
				"biomes": "#minecraft:is_savanna"
			}
		""".trimIndent()
	) {
		matchingBiomes(Tags.Worldgen.Biome.IS_SAVANNA)
	}

	assertPredicate(
		"matching_block_tag", """
			{
				"type": "minecraft:matching_block_tag",
				"offset": [
					0,
					-1,
					0
				],
				"tag": "minecraft:dirt"
			}
		""".trimIndent()
	) {
		matchingBlockTag(Tags.Block.DIRT) { offset(0, -1, 0) }
	}

	assertPredicate(
		"matching_blocks", """
			{
				"type": "minecraft:matching_blocks",
				"blocks": [
					"minecraft:stone",
					"minecraft:deepslate"
				]
			}
		""".trimIndent()
	) {
		matchingBlocks(Blocks.STONE, Blocks.DEEPSLATE)
	}

	assertPredicate(
		"matching_blocks_list", """
			{
				"type": "minecraft:matching_blocks",
				"offset": [
					1,
					0,
					-1
				],
				"blocks": "minecraft:stone"
			}
		""".trimIndent()
	) {
		matchingBlocks(listOf(Blocks.STONE)) { offset(1, 0, -1) }
	}

	assertPredicate(
		"matching_block_tag_inline", """
			{
				"type": "minecraft:matching_blocks",
				"blocks": "#minecraft:logs"
			}
		""".trimIndent()
	) {
		matchingBlocks(Tags.Block.LOGS)
	}

	assertPredicate(
		"matching_fluids", """
			{
				"type": "minecraft:matching_fluids",
				"offset": [
					0,
					1,
					0
				],
				"fluids": "minecraft:water"
			}
		""".trimIndent()
	) {
		matchingFluids(Fluids.WATER) { offset(0, 1, 0) }
	}

	assertPredicate(
		"matching_fluids_list", """
			{
				"type": "minecraft:matching_fluids",
				"fluids": [
					"minecraft:water",
					"minecraft:lava"
				]
			}
		""".trimIndent()
	) {
		matchingFluids(listOf(Fluids.WATER, Fluids.LAVA))
	}

	assertPredicate(
		"not", """
			{
				"type": "minecraft:not",
				"predicate": {
					"type": "minecraft:solid"
				}
			}
		""".trimIndent()
	) {
		not { solid() }
	}

	assertPredicate(
		"not_multiple", """
			{
				"type": "minecraft:not",
				"predicate": {
					"type": "minecraft:all_of",
					"predicates": [
						{
							"type": "minecraft:solid"
						},
						{
							"type": "minecraft:replaceable"
						}
					]
				}
			}
		""".trimIndent()
	) {
		not {
			solid()
			replaceable()
		}
	}

	assertPredicate(
		"replaceable", """
			{
				"type": "minecraft:replaceable",
				"offset": [
					0,
					1,
					0
				]
			}
		""".trimIndent()
	) {
		replaceable { offset(0, 1, 0) }
	}

	assertPredicate(
		"solid", """
			{
				"type": "minecraft:solid"
			}
		""".trimIndent()
	) {
		solid()
	}

	assertPredicate(
		"true", """
			{
				"type": "minecraft:true"
			}
		""".trimIndent()
	) {
		alwaysTrue()
	}

	assertPredicate(
		"unobstructed", """
			{
				"type": "minecraft:unobstructed",
				"offset": [
					0,
					2,
					0
				]
			}
		""".trimIndent()
	) {
		unobstructed { offset(0, 2, 0) }
	}

	assertPredicate(
		"would_survive", """
			{
				"type": "minecraft:would_survive",
				"offset": [
					0,
					1,
					0
				],
				"state": {
					"Name": "minecraft:oak_sapling"
				}
			}
		""".trimIndent()
	) {
		wouldSurvive(blockState(Blocks.OAK_SAPLING)) { offset(0, 1, 0) }
	}

	assertPredicate(
		"single_predicate_inlined", """
			{
				"type": "minecraft:solid"
			}
		""".trimIndent()
	) {
		solid()
	}

	assertPredicate(
		"several_predicates_wrapped_in_all_of", """
			{
				"type": "minecraft:all_of",
				"predicates": [
					{
						"type": "minecraft:solid",
						"offset": [
							0,
							-1,
							0
						]
					},
					{
						"type": "minecraft:not",
						"predicate": {
							"type": "minecraft:matching_fluids",
							"fluids": "minecraft:water"
						}
					}
				]
			}
		""".trimIndent()
	) {
		solid { offset(0, -1, 0) }
		not { matchingFluids(Fluids.WATER) }
	}

	assertPredicate(
		"nested_composites", """
			{
				"type": "minecraft:any_of",
				"predicates": [
					{
						"type": "minecraft:all_of",
						"predicates": [
							{
								"type": "minecraft:solid"
							},
							{
								"type": "minecraft:inside_world_bounds"
							}
						]
					},
					{
						"type": "minecraft:matching_block_tag",
						"tag": "minecraft:dirt"
					}
				]
			}
		""".trimIndent()
	) {
		anyOf {
			allOf {
				solid()
				insideWorldBounds()
			}
			matchingBlockTag(Tags.Block.DIRT)
		}
	}

	assertPredicate(
		"empty_predicate_is_true", """
			{
				"type": "minecraft:true"
			}
		""".trimIndent()
	) {}
}

class BlockPredicateTests : FunSpec({
	test("block predicate") {
		dataPack("blockPredicate") {
			pretty()
			blockPredicateTests()
		}
	}
})
