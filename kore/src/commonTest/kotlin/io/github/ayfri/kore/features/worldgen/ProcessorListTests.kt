package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.intproviders.uniform
import io.github.ayfri.kore.features.worldgen.processorlist.processorList
import io.github.ayfri.kore.features.worldgen.processorlist.types.*
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier.*
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.positionpredicate.*
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.rule
import io.github.ayfri.kore.features.worldgen.ruletest.*
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.LootTables
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.set
import io.kotest.core.spec.style.FunSpec

fun DataPack.processorListTests() {
	processorList("every_processor") {
		blackstoneReplace()
		blockAge(0.5)
		blockIgnore(Blocks.AIR, Blocks.STRUCTURE_BLOCK)
		blockIgnore(listOf(blockState(Blocks.CHEST, "facing" to "north")))
		blockRot(0.75, Blocks.STONE_BRICKS, Tags.Block.STAIRS)
		blockRot(0.25)
		capped(6, Nop)
		capped(uniform(1, 4)) {
			blockAge(0.8)
		}
		gravity()
		gravity(HeightMap.OCEAN_FLOOR, offset = -1)
		jigsawReplacement()
		lavaSubmergedBlock()
		nop()
		protectedBlocks(Tags.Block.STONE_BRICKS)
		protectedBlocks(Blocks.STONE, Blocks.DIRT)
	}

	processorLists.last() assertsIs """
		{
			"processors": [
				{
					"processor_type": "minecraft:blackstone_replace"
				},
				{
					"processor_type": "minecraft:block_age",
					"mossiness": 0.5
				},
				{
					"processor_type": "minecraft:block_ignore",
					"blocks": [
						{
							"Name": "minecraft:air"
						},
						{
							"Name": "minecraft:structure_block"
						}
					]
				},
				{
					"processor_type": "minecraft:block_ignore",
					"blocks": [
						{
							"Name": "minecraft:chest",
							"Properties": {
								"facing": "north"
							}
						}
					]
				},
				{
					"processor_type": "minecraft:block_rot",
					"integrity": 0.75,
					"rottable_blocks": [
						"minecraft:stone_bricks",
						"#minecraft:stairs"
					]
				},
				{
					"processor_type": "minecraft:block_rot",
					"integrity": 0.25
				},
				{
					"processor_type": "minecraft:capped",
					"limit": 6,
					"delegate": {
						"processor_type": "minecraft:nop"
					}
				},
				{
					"processor_type": "minecraft:capped",
					"limit": {
						"type": "minecraft:uniform",
						"min_inclusive": 1,
						"max_inclusive": 4
					},
					"delegate": {
						"processor_type": "minecraft:block_age",
						"mossiness": 0.8
					}
				},
				{
					"processor_type": "minecraft:gravity",
					"heightmap": "WORLD_SURFACE_WG",
					"offset": 0
				},
				{
					"processor_type": "minecraft:gravity",
					"heightmap": "OCEAN_FLOOR",
					"offset": -1
				},
				{
					"processor_type": "minecraft:jigsaw_replacement"
				},
				{
					"processor_type": "minecraft:lava_submerged_block"
				},
				{
					"processor_type": "minecraft:nop"
				},
				{
					"processor_type": "minecraft:protected_blocks",
					"value": "#minecraft:stone_bricks"
				},
				{
					"processor_type": "minecraft:protected_blocks",
					"value": [
						"minecraft:stone",
						"minecraft:dirt"
					]
				}
			]
		}
	""".trimIndent()

	processorList("every_rule") {
		rules {
			rule {
				positionPredicate = alwaysTruePos()
				inputPredicate = blockMatch(Blocks.STONE_BRICKS)
				locationPredicate = tagMatch(Tags.Block.DIRT)
				outputState = blockState(Blocks.MOSSY_STONE_BRICKS)
				blockEntityModifier = clear()
			}

			rule {
				positionPredicate = linearPos(minDist = 0, maxDist = 8, minChance = 1.0, maxChance = 0.0)
				inputPredicate = blockStateMatch(blockState(Blocks.CHEST, "facing" to "north"))
				outputState = blockState(Blocks.BARREL)
				blockEntityModifier = appendLoot(LootTables.Chests.SIMPLE_DUNGEON)
			}

			rule {
				positionPredicate = axisAlignedLinearPos(Axis.X) {
					minDist = 0
					maxDist = 1
				}

				inputPredicate = randomBlockStateMatch(blockState(Blocks.STONE), 0.5)
				locationPredicate = randomBlockMatch(Blocks.DEEPSLATE, 0.25)
				outputState = blockState(Blocks.CHISELED_STONE_BRICKS)
				blockEntityModifier = appendStatic {
					this["test"] = "test"
				}
			}

			rule {
				blockEntityModifier = passthrough()
			}
		}

		rules(ProcessorRule(outputState = blockState(Blocks.GRANITE)))
	}

	processorLists.last() assertsIs """
		{
			"processors": [
				{
					"processor_type": "minecraft:rule",
					"rules": [
						{
							"position_predicate": {
								"predicate_type": "minecraft:always_true"
							},
							"location_predicate": {
								"predicate_type": "minecraft:tag_match",
								"tag": "#minecraft:dirt"
							},
							"input_predicate": {
								"predicate_type": "minecraft:block_match",
								"block": "minecraft:stone_bricks"
							},
							"output_state": {
								"Name": "minecraft:mossy_stone_bricks"
							},
							"block_entity_modifier": {
								"type": "minecraft:clear"
							}
						},
						{
							"position_predicate": {
								"predicate_type": "minecraft:linear_pos",
								"min_dist": 0,
								"max_dist": 8,
								"min_chance": 1.0,
								"max_chance": 0.0
							},
							"location_predicate": {
								"predicate_type": "minecraft:always_true"
							},
							"input_predicate": {
								"predicate_type": "minecraft:blockstate_match",
								"block_state": {
									"Name": "minecraft:chest",
									"Properties": {
										"facing": "north"
									}
								}
							},
							"output_state": {
								"Name": "minecraft:barrel"
							},
							"block_entity_modifier": {
								"type": "minecraft:append_loot",
								"loot_table": "minecraft:chests/simple_dungeon"
							}
						},
						{
							"position_predicate": {
								"predicate_type": "minecraft:axis_aligned_linear_pos",
								"axis": "x",
								"min_dist": 0,
								"max_dist": 1
							},
							"location_predicate": {
								"predicate_type": "minecraft:random_block_match",
								"block": "minecraft:deepslate",
								"probability": 0.25
							},
							"input_predicate": {
								"predicate_type": "minecraft:random_blockstate_match",
								"block_state": {
									"Name": "minecraft:stone"
								},
								"probability": 0.5
							},
							"output_state": {
								"Name": "minecraft:chiseled_stone_bricks"
							},
							"block_entity_modifier": {
								"type": "minecraft:append_static",
								"data": {
									"test": "test"
								}
							}
						},
						{
							"location_predicate": {
								"predicate_type": "minecraft:always_true"
							},
							"input_predicate": {
								"predicate_type": "minecraft:always_true"
							},
							"output_state": {
								"Name": "minecraft:stone"
							},
							"block_entity_modifier": {
								"type": "minecraft:passthrough"
							}
						}
					]
				},
				{
					"processor_type": "minecraft:rule",
					"rules": [
						{
							"location_predicate": {
								"predicate_type": "minecraft:always_true"
							},
							"input_predicate": {
								"predicate_type": "minecraft:always_true"
							},
							"output_state": {
								"Name": "minecraft:granite"
							}
						}
					]
				}
			]
		}
	""".trimIndent()
}

class ProcessorListTests : FunSpec({
	test("processor list") {
		dataPack("processorList") {
			pretty()
			processorListTests()
		}
	}
})
