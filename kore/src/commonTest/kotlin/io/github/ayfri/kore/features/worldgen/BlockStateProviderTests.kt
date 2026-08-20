package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.blockpredicate.hasSturdyFace
import io.github.ayfri.kore.features.worldgen.blockpredicate.matchingBlocks
import io.github.ayfri.kore.features.worldgen.blockpredicate.solid
import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.*
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeaturesBuilder
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.SimpleBlock
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.simpleBlock
import io.github.ayfri.kore.features.worldgen.intproviders.uniform
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

private fun DataPack.assertProvider(name: String, expected: String, block: SimpleBlock.() -> BlockStateProvider) {
	configuredFeaturesBuilder.simpleBlock(name) { toPlace = block() }

	val template = """
		{
			"type": "minecraft:simple_block",
			"config": {
				"to_place": <provider>,
				"schedule_tick": false
			}
		}
	""".trimIndent()

	configuredFeatures.last() assertsIs template.replace("<provider>", expected.prependIndent("\t\t").trimStart())
}

fun DataPack.blockStateProviderTests() {
	assertProvider(
		"simple_state_provider", """
			{
				"type": "minecraft:simple_state_provider",
				"state": {
					"Name": "minecraft:dandelion"
				}
			}
		""".trimIndent()
	) { simpleStateProvider(Blocks.DANDELION) }

	assertProvider(
		"simple_state_provider_properties", """
			{
				"type": "minecraft:simple_state_provider",
				"state": {
					"Name": "minecraft:oak_log",
					"Properties": {
						"axis": "y"
					}
				}
			}
		""".trimIndent()
	) { simpleStateProvider(Blocks.OAK_LOG, mapOf("axis" to "y")) }

	assertProvider(
		"rotated_block_provider", """
			{
				"type": "minecraft:rotated_block_provider",
				"state": {
					"Name": "minecraft:oak_log"
				}
			}
		""".trimIndent()
	) { rotatedBlockProvider(Blocks.OAK_LOG) }

	assertProvider(
		"weighted_state_provider", """
			{
				"type": "minecraft:weighted_state_provider",
				"entries": [
					{
						"weight": 3,
						"data": {
							"type": "minecraft:simple_state_provider",
							"state": {
								"Name": "minecraft:dandelion"
							}
						}
					},
					{
						"weight": 1,
						"data": {
							"type": "minecraft:simple_state_provider",
							"state": {
								"Name": "minecraft:poppy"
							}
						}
					},
					{
						"weight": 2,
						"data": {
							"type": "minecraft:rotated_block_provider",
							"state": {
								"Name": "minecraft:oak_log"
							}
						}
					}
				]
			}
		""".trimIndent()
	) {
		weightedStateProvider {
			entry(Blocks.DANDELION, weight = 3)
			entry(blockState(Blocks.POPPY))
			entry(weight = 2) {
				data = rotatedBlockProvider(Blocks.OAK_LOG)
			}
		}
	}

	assertProvider(
		"noise_provider", """
			{
				"type": "minecraft:noise_provider",
				"seed": 2345,
				"noise": {
					"firstOctave": -3,
					"amplitudes": [
						1.0,
						1.0
					]
				},
				"scale": 0.05,
				"states": [
					{
						"Name": "minecraft:moss_block"
					},
					{
						"Name": "minecraft:stone"
					}
				]
			}
		""".trimIndent()
	) {
		noiseProvider {
			seed = 2345
			scale = 0.05
			noise(-3, 1.0, 1.0)
			states(Blocks.MOSS_BLOCK, Blocks.STONE)
		}
	}

	assertProvider(
		"dual_noise_provider", """
			{
				"type": "minecraft:dual_noise_provider",
				"seed": 2345,
				"noise": {
					"firstOctave": -3,
					"amplitudes": [
						1.0
					]
				},
				"scale": 0.05,
				"variety": {
					"type": "minecraft:uniform",
					"min_inclusive": 1,
					"max_inclusive": 3
				},
				"slow_noise": {
					"firstOctave": -10,
					"amplitudes": [
						1.0,
						1.0
					]
				},
				"slow_scale": 0.005,
				"states": [
					{
						"Name": "minecraft:crimson_roots"
					},
					{
						"Name": "minecraft:warped_roots"
					}
				]
			}
		""".trimIndent()
	) {
		dualNoiseProvider {
			seed = 2345
			scale = 0.05
			slowScale = 0.005
			noise(-3, 1.0)
			slowNoise(-10, 1.0, 1.0)
			variety(1, 3)
			states(Blocks.CRIMSON_ROOTS, Blocks.WARPED_ROOTS)
		}
	}

	assertProvider(
		"noise_threshold_provider", """
			{
				"type": "minecraft:noise_threshold_provider",
				"seed": 2345,
				"noise": {
					"firstOctave": -3,
					"amplitudes": [
						1.0
					]
				},
				"scale": 0.05,
				"threshold": -0.8,
				"high_chance": 0.31,
				"default_state": {
					"Name": "minecraft:grass_block"
				},
				"low_states": [
					{
						"Name": "minecraft:podzol"
					}
				],
				"high_states": [
					{
						"Name": "minecraft:coarse_dirt"
					}
				]
			}
		""".trimIndent()
	) {
		noiseThresholdProvider {
			seed = 2345
			scale = 0.05
			threshold = -0.8
			highChance = 0.31
			noise(-3, 1.0)
			defaultState = blockState(Blocks.GRASS_BLOCK)
			lowStates(Blocks.PODZOL)
			highStates(Blocks.COARSE_DIRT)
		}
	}

	assertProvider(
		"randomized_int_state_provider", """
			{
				"type": "minecraft:randomized_int_state_provider",
				"property": "age",
				"values": {
					"type": "minecraft:uniform",
					"min_inclusive": 0,
					"max_inclusive": 7
				},
				"source": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:wheat"
					}
				}
			}
		""".trimIndent()
	) {
		randomizedIntStateProvider("age") {
			values(0, 7)
			source = simpleStateProvider(Blocks.WHEAT)
		}
	}

	assertProvider(
		"randomized_int_state_provider_values", """
			{
				"type": "minecraft:randomized_int_state_provider",
				"property": "berries",
				"values": {
					"type": "minecraft:uniform",
					"min_inclusive": 0,
					"max_inclusive": 1
				},
				"source": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:cave_vines"
					}
				}
			}
		""".trimIndent()
	) { randomizedIntStateProvider("berries", uniform(0, 1)) { source = simpleStateProvider(Blocks.CAVE_VINES) } }

	assertProvider(
		"rule_based_state_provider", """
			{
				"type": "minecraft:rule_based_state_provider",
				"fallback": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:dirt"
					}
				},
				"rules": [
					{
						"if_true": {
							"type": "minecraft:has_sturdy_face",
							"direction": "down"
						},
						"then": {
							"type": "minecraft:simple_state_provider",
							"state": {
								"Name": "minecraft:grass_block"
							}
						}
					},
					{
						"if_true": {
							"type": "minecraft:matching_blocks",
							"blocks": "minecraft:stone"
						},
						"then": {
							"type": "minecraft:simple_state_provider",
							"state": {
								"Name": "minecraft:podzol"
							}
						}
					}
				]
			}
		""".trimIndent()
	) {
		ruleBasedStateProvider {
			fallback = simpleStateProvider(Blocks.DIRT)
			rule(simpleStateProvider(Blocks.GRASS_BLOCK)) {
				hasSturdyFace(Direction.DOWN)
			}
			rule {
				ifTrue { matchingBlocks(Blocks.STONE) }
				then = simpleStateProvider(Blocks.PODZOL)
			}
		}
	}

	assertProvider(
		"rule_based_state_provider_empty", """
			{
				"type": "minecraft:rule_based_state_provider",
				"rules": []
			}
		""".trimIndent()
	) { ruleBasedStateProvider() }

	assertProvider(
		"nested_providers", """
			{
				"type": "minecraft:weighted_state_provider",
				"entries": [
					{
						"weight": 1,
						"data": {
							"type": "minecraft:randomized_int_state_provider",
							"property": "age",
							"values": {
								"type": "minecraft:uniform",
								"min_inclusive": 0,
								"max_inclusive": 7
							},
							"source": {
								"type": "minecraft:simple_state_provider",
								"state": {
									"Name": "minecraft:wheat"
								}
							}
						}
					},
					{
						"weight": 4,
						"data": {
							"type": "minecraft:rule_based_state_provider",
							"rules": [
								{
									"if_true": {
										"type": "minecraft:solid"
									},
									"then": {
										"type": "minecraft:simple_state_provider",
										"state": {
											"Name": "minecraft:carrots"
										}
									}
								}
							]
						}
					}
				]
			}
		""".trimIndent()
	) {
		weightedStateProvider {
			entry(
				randomizedIntStateProvider("age") {
					values(0, 7)
					source = simpleStateProvider(Blocks.WHEAT)
				}
			)
			entry(
				ruleBasedStateProvider {
					rule(simpleStateProvider(Blocks.CARROTS)) { solid() }
				},
				weight = 4,
			)
		}
	}
}

class BlockStateProviderTests : FunSpec({
	test("block state provider") {
		dataPack("blockStateProvider") {
			pretty()
			blockStateProviderTests()
		}
	}
})
