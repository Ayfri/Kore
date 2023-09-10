package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.processorlist.processorList
import io.github.ayfri.kore.features.worldgen.processorlist.types.Nop
import io.github.ayfri.kore.features.worldgen.processorlist.types.capped
import io.github.ayfri.kore.features.worldgen.processorlist.types.gravity
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier.appendStatic
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.positionpredicate.axisAlignedLinearPos
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.rule
import io.github.ayfri.kore.features.worldgen.processorlist.types.rules
import io.github.ayfri.kore.features.worldgen.ruletest.randomBlockStateMatch
import io.github.ayfri.kore.utils.set

fun DataPack.processorListTests() {
	processorList("test") {
		rules {
			rule {
				positionPredicate = axisAlignedLinearPos(axis = Axis.X) {
					minDist = 1
				}

				inputPredicate = randomBlockStateMatch()
				outputState = blockStateStone()

				blockEntityModifier = appendStatic {
					this["test"] = "test"
				}
			}
		}

		gravity(HeightMap.OCEAN_FLOOR)

		capped(6, Nop)
	}

	processorLists.last() assertsIs """
		{
			"processors": [
				{
					"processor_type": "minecraft:rule",
					"rules": [
						{
							"position_predicate": {
								"predicate_type": "minecraft:axis_aligned_linear_pos",
								"axis": "x",
								"min_dist": 1
							},
							"location_predicate": {
								"predicate_type": "minecraft:always_true"
							},
							"input_predicate": {
								"predicate_type": "minecraft:random_blockstate_match",
								"block_state": {
									"Name": "minecraft:stone"
								},
								"probability": 0.0
							},
							"output_state": {
								"Name": "minecraft:stone"
							},
							"block_entity_modifier": {
								"type": "minecraft:append_static",
								"data": {
									"test": "test"
								}
							}
						}
					]
				},
				{
					"processor_type": "minecraft:gravity",
					"heightmap": "OCEAN_FLOOR",
					"offset": 0
				},
				{
					"processor_type": "minecraft:capped",
					"limit": 6,
					"delegate": {
						"processor_type": "minecraft:nop"
					}
				}
			]
		}
	""".trimIndent()
}
