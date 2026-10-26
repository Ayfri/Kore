package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsThrows
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
import io.github.ayfri.kore.generated.BlockStateProviders
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.utils.decodeFromSnbt
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf

private fun DataPack.assertProvider(name: String, expected: String, block: SimpleBlock.() -> BlockStateProvider) {
	configuredFeaturesBuilder.simpleBlock(name) { toPlace = block() }

	val template = """
		{
			"type": "minecraft:simple_block",
			"to_place": <provider>,
			"schedule_tick": false
		}
	""".trimIndent()

	configuredFeatures.last() assertsIs template.replace("<provider>", expected.prependIndent("\t").trimStart())
}

fun DataPack.blockStateProviderTests() {
	assertProvider(
		"simple_state_provider", """
			{
				"type": "minecraft:simple",
				"state": "minecraft:dandelion"
			}
		""".trimIndent()
	) { simpleStateProvider(Blocks.DANDELION) }

	assertProvider(
		"simple_state_provider_properties", """
			{
				"type": "minecraft:simple",
				"state": {
					"id": "minecraft:oak_log",
					"properties": {
						"axis": "y"
					}
				}
			}
		""".trimIndent()
	) { simpleStateProvider(Blocks.OAK_LOG, mapOf("axis" to "y")) }

	assertProvider(
		"copy_properties_provider", """
			{
				"type": "minecraft:copy_properties",
				"source": {
					"type": "minecraft:simple",
					"state": "minecraft:waxed_copper_block"
				}
			}
		""".trimIndent()
	) { copyPropertiesProvider(simpleStateProvider(Blocks.WAXED_COPPER_BLOCK)) }

	assertProvider(
		"rotated_block_provider", """
			{
				"type": "minecraft:rotated",
				"state": {
					"type": "minecraft:simple",
					"state": "minecraft:oak_log"
				}
			}
		""".trimIndent()
	) { rotatedBlockProvider(Blocks.OAK_LOG) }

	assertProvider(
		"rotated_block_provider_direction", """
			{
				"type": "minecraft:rotated",
				"direction": "north",
				"state": {
					"type": "minecraft:simple",
					"state": "minecraft:dispenser"
				}
			}
		""".trimIndent()
	) { rotatedBlockProvider(Blocks.DISPENSER, Direction.NORTH) }

	assertProvider(
		"rotated_block_provider_nested", """
			{
				"type": "minecraft:rotated",
				"direction": "up",
				"state": {
					"type": "minecraft:weighted",
					"entries": [
						{
							"weight": 1,
							"data": {
								"type": "minecraft:simple",
								"state": "minecraft:oak_log"
							}
						}
					]
				}
			}
		""".trimIndent()
	) {
		rotatedBlockProvider(
			weightedStateProvider { entry(Blocks.OAK_LOG) },
			Direction.UP,
		)
	}

	assertProvider(
		"random_block_provider", """
			{
				"type": "minecraft:random_block",
				"blocks": [
					"minecraft:dandelion",
					"minecraft:poppy"
				]
			}
		""".trimIndent()
	) { randomBlockProvider(Blocks.DANDELION, Blocks.POPPY) }

	assertProvider(
		"random_block_provider_tag", """
			{
				"type": "minecraft:random_block",
				"blocks": "#minecraft:flowers"
			}
		""".trimIndent()
	) { randomBlockProvider(Tags.Block.FLOWERS) }

	assertProvider(
		"weighted_state_provider", """
			{
				"type": "minecraft:weighted",
				"entries": [
					{
						"weight": 3,
						"data": {
							"type": "minecraft:simple",
							"state": "minecraft:dandelion"
						}
					},
					{
						"weight": 1,
						"data": {
							"type": "minecraft:simple",
							"state": "minecraft:poppy"
						}
					},
					{
						"weight": 2,
						"data": {
							"type": "minecraft:rotated",
							"state": {
								"type": "minecraft:simple",
								"state": "minecraft:oak_log"
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
				"type": "minecraft:noise",
				"seed": 2345,
				"noise": {
					"amplitude_modifiers": [
						1.0,
						1.0
					],
					"base_octave": -3,
					"octave_count": 2
				},
				"scale": 0.05,
				"states": [
					"minecraft:moss_block",
					"minecraft:stone"
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
				"type": "minecraft:dual_noise",
				"seed": 2345,
				"noise": {
					"amplitude_modifiers": [
						1.0
					],
					"base_octave": -3,
					"octave_count": 1
				},
				"scale": 0.05,
				"variety": {
					"type": "minecraft:uniform",
					"min_inclusive": 1,
					"max_inclusive": 3
				},
				"slow_noise": {
					"amplitude_modifiers": [
						1.0,
						1.0
					],
					"base_octave": -10,
					"octave_count": 2
				},
				"slow_scale": 0.005,
				"states": [
					"minecraft:crimson_roots",
					"minecraft:warped_roots"
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
		"dual_noise_provider_plain_variety", """
			{
				"type": "minecraft:dual_noise",
				"seed": 2345,
				"noise": {
					"amplitude_modifiers": [
						1.0
					],
					"base_octave": -3,
					"octave_count": 1
				},
				"scale": 0.05,
				"variety": 2,
				"slow_noise": {
					"amplitude_modifiers": [
						1.0,
						1.0
					],
					"base_octave": -10,
					"octave_count": 2
				},
				"slow_scale": 0.005,
				"states": [
					"minecraft:crimson_roots",
					"minecraft:warped_roots"
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
			variety(2)
			states(Blocks.CRIMSON_ROOTS, Blocks.WARPED_ROOTS)
		}
	}

	assertProvider(
		"noise_threshold_provider", """
			{
				"type": "minecraft:noise_threshold",
				"seed": 2345,
				"noise": {
					"amplitude_modifiers": [
						1.0
					],
					"base_octave": -3,
					"octave_count": 1
				},
				"scale": 0.05,
				"threshold": -0.8,
				"high_chance": 0.31,
				"default_state": "minecraft:grass_block",
				"low_states": [
					"minecraft:podzol"
				],
				"high_states": [
					"minecraft:coarse_dirt"
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
				"type": "minecraft:randomized_int",
				"property": "age",
				"values": {
					"type": "minecraft:uniform",
					"min_inclusive": 0,
					"max_inclusive": 7
				},
				"source": {
					"type": "minecraft:simple",
					"state": "minecraft:wheat"
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
				"type": "minecraft:randomized_int",
				"property": "berries",
				"values": {
					"type": "minecraft:uniform",
					"min_inclusive": 0,
					"max_inclusive": 1
				},
				"source": {
					"type": "minecraft:simple",
					"state": "minecraft:cave_vines"
				}
			}
		""".trimIndent()
	) { randomizedIntStateProvider("berries", uniform(0, 1)) { source = simpleStateProvider(Blocks.CAVE_VINES) } }

	assertProvider(
		"randomized_int_state_provider_constant_values", """
			{
				"type": "minecraft:randomized_int",
				"property": "age",
				"values": 3,
				"source": {
					"type": "minecraft:simple",
					"state": "minecraft:wheat"
				}
			}
		""".trimIndent()
	) { randomizedIntStateProvider("age", values = 3) { source = simpleStateProvider(Blocks.WHEAT) } }

	assertProvider(
		"rule_based_state_provider", """
			{
				"type": "minecraft:rule_based",
				"fallback": {
					"type": "minecraft:simple",
					"state": "minecraft:dirt"
				},
				"rules": [
					{
						"if_true": {
							"type": "minecraft:has_sturdy_face",
							"direction": "down"
						},
						"then": {
							"type": "minecraft:simple",
							"state": "minecraft:grass_block"
						}
					},
					{
						"if_true": {
							"type": "minecraft:matching_blocks",
							"blocks": "minecraft:stone"
						},
						"then": {
							"type": "minecraft:simple",
							"state": "minecraft:podzol"
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
				"type": "minecraft:rule_based",
				"rules": []
			}
		""".trimIndent()
	) { ruleBasedStateProvider() }

	assertProvider(
		"nested_providers", """
			{
				"type": "minecraft:weighted",
				"entries": [
					{
						"weight": 1,
						"data": {
							"type": "minecraft:randomized_int",
							"property": "age",
							"values": {
								"type": "minecraft:uniform",
								"min_inclusive": 0,
								"max_inclusive": 7
							},
							"source": {
								"type": "minecraft:simple",
								"state": "minecraft:wheat"
							}
						}
					},
					{
						"weight": 4,
						"data": {
							"type": "minecraft:rule_based",
							"rules": [
								{
									"if_true": {
										"type": "minecraft:solid"
									},
									"then": {
										"type": "minecraft:simple",
										"state": "minecraft:carrots"
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

fun DataPack.blockStateProviderFileTests() {
	assertProvider("reference", "\"minecraft:soil_beneath_tree\"") { reference(BlockStateProviders.SOIL_BENEATH_TREE) }

	assertProvider(
		"reference_in_rule", """
			{
				"type": "minecraft:rule_based",
				"rules": [
					{
						"if_true": {
							"type": "minecraft:solid"
						},
						"then": "minecraft:podzol_beneath_tree"
					}
				]
			}
		""".trimIndent()
	) {
		ruleBasedStateProvider {
			rule(reference(BlockStateProviders.PODZOL_BENEATH_TREE)) { solid() }
		}
	}

	val dirtOrGrass = blockStateProvider("dirt_or_grass") {
		weightedStateProvider {
			entry(Blocks.DIRT, weight = 3)
			entry(Blocks.GRASS_BLOCK)
		}
	}

	dirtOrGrass.asId() assertsIs "blockStateProvider:dirt_or_grass"

	blockStateProviders.last() assertsIs """
		{
			"type": "minecraft:weighted",
			"entries": [
				{
					"weight": 3,
					"data": {
						"type": "minecraft:simple",
						"state": "minecraft:dirt"
					}
				},
				{
					"weight": 1,
					"data": {
						"type": "minecraft:simple",
						"state": "minecraft:grass_block"
					}
				}
			]
		}
	""".trimIndent()

	assertProvider("reference_to_file", "\"blockStateProvider:dirt_or_grass\"") { reference(dirtOrGrass) }

	blockStateProvider("reference_file") { reference(BlockStateProviders.SOIL_BENEATH_TREE) }

	assertsThrows("Block state provider file 'reference_file' must hold an inline provider, not a reference.") {
		blockStateProviders.last().generateJson(this)
	}

	val frostedIce = """
		{
			"type": "minecraft:simple",
			"state": {
				"id": "minecraft:frosted_ice",
				"properties": {
					"age": "0"
				}
			}
		}
	""".trimIndent()

	val fromState = jsonEncoder.decodeFromString(
		BlockStateProvider.serializer(),
		"""{"id":"minecraft:frosted_ice","properties":{"age":"0"}}"""
	)
	fromState.shouldBeInstanceOf<SimpleBlockStateProvider>()
	jsonEncoder.encodeToString(BlockStateProvider.serializer(), fromState) assertsIs frostedIce
	jsonEncoder.encodeToString(
		BlockStateProvider.serializer(),
		decodeFromSnbt<BlockStateProvider>("""{id:"minecraft:frosted_ice",properties:{age:"0"}}""")
	) assertsIs frostedIce
	jsonEncoder.encodeToString(
		BlockStateProvider.serializer(),
		jsonEncoder.decodeFromString(BlockStateProvider.serializer(), frostedIce)
	) assertsIs frostedIce

	jsonEncoder.decodeFromString(BlockStateProvider.serializer(), "\"my_pack:soil\"")
		.shouldBeInstanceOf<BlockStateProviderReference>().id.asString() assertsIs "my_pack:soil"
	jsonEncoder.encodeToString(BlockStateProvider.serializer(), BlockStateProviderReference(BlockStateProviders.FLOWER_PLAIN)) assertsIs "\"minecraft:flower_plain\""
}

class BlockStateProviderTests : FunSpec({
	test("block state provider") {
		dataPack("blockStateProvider") {
			pretty()
			blockStateProviderTests()
		}
	}

	test("block state provider files and references") {
		dataPack("blockStateProvider") {
			pretty()
			blockStateProviderFileTests()
		}
	}
})
