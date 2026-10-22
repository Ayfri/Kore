package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.loottables.lootTable
import io.github.ayfri.kore.features.loottables.pool
import io.github.ayfri.kore.features.predicates.conditions.SurvivesExplosion
import io.github.ayfri.kore.features.predicates.conditions.matchBlock
import io.github.ayfri.kore.features.predicates.providers.*
import io.github.ayfri.kore.features.predicates.sub.state
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.generated.NumberProviders
import io.github.ayfri.kore.generated.arguments.tagged.NumberProviderTagArgument
import io.github.ayfri.kore.generated.arguments.types.NumberProviderArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.utils.pretty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

fun DataPack.numberProviderTests() {
	lootTable("binomial_test") {
		pool {
			rolls = binomial(5f, 0.5f)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:binomial",
						"n": 5.0,
						"p": 0.5
					}
				}
			]
		}
	""".trimIndent()

	lootTable("conditional_test") {
		pool {
			rolls = conditionalNumber(5f, 2f) {
				matchBlock(Blocks.COMPOSTER) {
					state("level", "0")
				}
			}
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:conditional",
						"condition": {
							"type": "minecraft:match_block",
							"blocks": "minecraft:composter",
							"state": {
								"level": "0"
							}
						},
						"on_false": 2.0,
						"on_true": 5.0
					}
				}
			]
		}
	""".trimIndent()

	lootTable("conditional_without_on_false_test") {
		pool {
			rolls = conditionalNumber(SurvivesExplosion, uniform(1f, 3f))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:conditional",
						"condition": {
							"type": "minecraft:survives_explosion"
						},
						"on_true": {
							"type": "minecraft:uniform",
							"min": 1.0,
							"max": 3.0
						}
					}
				}
			]
		}
	""".trimIndent()

	lootTable("conditional_reference_test") {
		pool {
			rolls = conditionalNumber(PredicateArgument("block/fast_cooking"), 800f, 1600f)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:conditional",
						"condition": "minecraft:block/fast_cooking",
						"on_false": 1600.0,
						"on_true": 800.0
					}
				}
			]
		}
	""".trimIndent()

	lootTable("case_reference_test") {
		pool {
			rolls = numberDispatcher {
				case(PredicateArgument("is_night", "my_pack"), 2f)
				case(SurvivesExplosion, 1f)
			}
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:number_dispatcher",
						"cases": [
							{
								"condition": "my_pack:is_night",
								"number_provider": 2.0
							},
							{
								"condition": {
									"type": "minecraft:survives_explosion"
								},
								"number_provider": 1.0
							}
						]
					}
				}
			]
		}
	""".trimIndent()

	lootTable("constant_test") {
		pool {
			rolls = constant(5f)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": 5.0
				}
			]
		}
	""".trimIndent()

	lootTable("enchantment_level_test") {
		pool {
			rolls = enchantmentLevel(5)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:enchantment_level",
						"amount": 5
					}
				}
			]
		}
	""".trimIndent()

	lootTable("environment_attribute_test") {
		pool {
			rolls = environmentAttribute(EnvironmentAttributes.Gameplay.BABY_VILLAGER_ACTIVITY)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:environment_attribute",
						"attribute": "minecraft:gameplay/baby_villager_activity"
					}
				}
			]
		}
	""".trimIndent()

	lootTable("number_dispatcher_test") {
		pool {
			rolls = numberDispatcher(default = weightedListNumber {
				entry(30, 1f)
				entry(70, 0f)
			}) {
				case(1f) {
					matchBlock(Blocks.COMPOSTER) {
						state("level", "0")
					}
				}
				case(SurvivesExplosion, constant(2f))
			}
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:number_dispatcher",
						"cases": [
							{
								"condition": {
									"type": "minecraft:match_block",
									"blocks": "minecraft:composter",
									"state": {
										"level": "0"
									}
								},
								"number_provider": 1.0
							},
							{
								"condition": {
									"type": "minecraft:survives_explosion"
								},
								"number_provider": 2.0
							}
						],
						"default": {
							"type": "minecraft:weighted_list",
							"distribution": [
								{
									"data": 1.0,
									"weight": 30
								},
								{
									"data": 0.0,
									"weight": 70
								}
							]
						}
					}
				}
			]
		}
	""".trimIndent()

	lootTable("number_dispatcher_without_default_test") {
		pool {
			rolls = numberDispatcher {
				case(SurvivesExplosion, 4f)
			}
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:number_dispatcher",
						"cases": [
							{
								"condition": {
									"type": "minecraft:survives_explosion"
								},
								"number_provider": 4.0
							}
						]
					}
				}
			]
		}
	""".trimIndent()

	lootTable("score_context_test") {
		pool {
			rolls = scoreNumber("kills", EntityTarget.THIS)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:score",
						"target": {
							"type": "minecraft:context",
							"target": "this"
						},
						"score": "kills"
					}
				}
			]
		}
	""".trimIndent()

	lootTable("score_fixed_test") {
		pool {
			rolls = scoreNumber("kills", name = "Steve")
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:score",
						"target": {
							"type": "minecraft:fixed",
							"name": "Steve"
						},
						"score": "kills"
					}
				}
			]
		}
	""".trimIndent()

	lootTable("score_provider_test") {
		pool {
			rolls = scoreNumber("kills", fixedScore("Steve"))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:score",
						"target": {
							"type": "minecraft:fixed",
							"name": "Steve"
						},
						"score": "kills"
					}
				}
			]
		}
	""".trimIndent()

	lootTable("score_scaled_test") {
		pool {
			rolls = scoreNumber("kills", EntityTarget.ATTACKING_PLAYER, scale = 0.5f)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:score",
						"target": {
							"type": "minecraft:context",
							"target": "attacking_player"
						},
						"score": "kills",
						"scale": 0.5
					}
				}
			]
		}
	""".trimIndent()

	lootTable("storage_test") {
		pool {
			rolls = storageNumber("my_pack:data", "player.health")
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:storage",
						"storage": "my_pack:data",
						"path": "player.health"
					}
				}
			]
		}
	""".trimIndent()

	lootTable("sum_test") {
		pool {
			rolls = sum(constant(1f), uniform(1f, 3f))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:sum",
						"operands": [
							1.0,
							{
								"type": "minecraft:uniform",
								"min": 1.0,
								"max": 3.0
							}
						]
					}
				}
			]
		}
	""".trimIndent()

	lootTable("uniform_test") {
		pool {
			rolls = uniform(1f, 5f)
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:uniform",
						"min": 1.0,
						"max": 5.0
					}
				}
			]
		}
	""".trimIndent()

	lootTable("weighted_list_test") {
		pool {
			rolls = weightedListNumber {
				entry(30, 1f)
				entry(70, 0f)
			}
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:weighted_list",
						"distribution": [
							{
								"data": 1.0,
								"weight": 30
							},
							{
								"data": 0.0,
								"weight": 70
							}
						]
					}
				}
			]
		}
	""".trimIndent()

	lootTable("reference_test") {
		pool {
			rolls = reference(NumberProviderArgument("r", "my_pack"))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": "my_pack:r"
				}
			]
		}
	""".trimIndent()

	lootTable("reference_nested_test") {
		pool {
			rolls = uniform(reference(NumberProviders.Cooking.TIME_COAL), constant(5f))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:uniform",
						"min": "minecraft:cooking/time_coal",
						"max": 5.0
					}
				}
			]
		}
	""".trimIndent()
	roundTrip(lootTables.last())

	lootTable("sum_references_test") {
		pool {
			rolls = sum(reference(NumberProviderArgument("a", "my_pack")), reference(NumberProviderArgument("b", "my_pack")))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:sum",
						"operands": [
							"my_pack:a",
							"my_pack:b"
						]
					}
				}
			]
		}
	""".trimIndent()

	lootTable("sum_tag_test") {
		pool {
			rolls = sum(NumberProviderTagArgument("t", "my_pack"))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:sum",
						"operands": "#my_pack:t"
					}
				}
			]
		}
	""".trimIndent()
	roundTrip(lootTables.last())

	lootTable("sum_mixed_test") {
		pool {
			rolls = sum(reference(NumberProviderArgument("a", "my_pack")), constant(1f))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:sum",
						"operands": [
							"my_pack:a",
							1.0
						]
					}
				}
			]
		}
	""".trimIndent()
	roundTrip(lootTables.last())

	lootTable("product_test") {
		pool {
			rolls = product(reference(NumberProviderArgument("a", "my_pack")), constant(2f))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:product",
						"operands": [
							"my_pack:a",
							2.0
						]
					}
				}
			]
		}
	""".trimIndent()
	roundTrip(lootTables.last())

	lootTable("maximum_tag_test") {
		pool {
			rolls = maximum(NumberProviderTagArgument("t", "my_pack"))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:maximum",
						"operands": "#my_pack:t"
					}
				}
			]
		}
	""".trimIndent()
	roundTrip(lootTables.last())

	lootTable("minimum_single_test") {
		pool {
			rolls = minimum(constant(1f))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:minimum",
						"operands": 1.0
					}
				}
			]
		}
	""".trimIndent()
	roundTrip(lootTables.last())

	lootTable("average_test") {
		pool {
			rolls = average(listOf(constant(1f), uniform(1f, 3f)))
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [],
					"rolls": {
						"type": "minecraft:average",
						"operands": [
							1.0,
							{
								"type": "minecraft:uniform",
								"min": 1.0,
								"max": 3.0
							}
						]
					}
				}
			]
		}
	""".trimIndent()
	roundTrip(lootTables.last())

	jsonEncoder.decodeFromString(NumberProvider.serializer(), """{"type":"minecraft:average","operands":1}""")
		.shouldBeInstanceOf<AverageNumberProvider>()
	jsonEncoder.decodeFromString(NumberProvider.serializer(), """{"type":"minecraft:maximum","operands":[1,2]}""")
		.shouldBeInstanceOf<MaximumNumberProvider>().operands.size shouldBe 2
	jsonEncoder.decodeFromString(NumberProvider.serializer(), """{"type":"minecraft:minimum","operands":"#my_pack:t"}""")
		.shouldBeInstanceOf<MinimumNumberProvider>()
	jsonEncoder.decodeFromString(NumberProvider.serializer(), """{"type":"minecraft:product","operands":[1,2]}""")
		.shouldBeInstanceOf<ProductNumberProvider>().operands.size shouldBe 2

	shouldThrow<IllegalArgumentException> { lootTable("product_empty_test") { pool { rolls = product() } } }

	jsonEncoder.decodeFromString(NumberProvider.serializer(), "5.0") assertsIs ConstantNumberProvider(5f)
	jsonEncoder.decodeFromString(NumberProvider.serializer(), """{"type":"minecraft:constant","value":5.0}""") assertsIs
		ConstantNumberProvider(5f)

	jsonEncoder.decodeFromString(NumberProvider.serializer(), "\"my_pack:r\"")
		.shouldBeInstanceOf<NumberProviderReference>().id
		.shouldBeInstanceOf<NumberProviderArgument>().asString() assertsIs "my_pack:r"

	jsonEncoder.decodeFromString(NumberProvider.serializer(), "\"#my_pack:t\"")
		.shouldBeInstanceOf<NumberProviderReference>().id
		.shouldBeInstanceOf<NumberProviderTagArgument>().asString() assertsIs "#my_pack:t"

	jsonEncoder.decodeFromString(NumberProvider.serializer(), """{"type":"minecraft:sum","operands":"#my_pack:t"}""")
		.shouldBeInstanceOf<SumNumberProvider>().operands.single()
		.shouldBeInstanceOf<NumberProviderReference>().id
		.shouldBeInstanceOf<NumberProviderTagArgument>().asString() assertsIs "#my_pack:t"

	shouldThrow<IllegalArgumentException> { lootTable("sum_empty_test") { pool { rolls = sum() } } }
	shouldThrowAny { jsonEncoder.decodeFromString(NumberProvider.serializer(), """{"type":"minecraft:sum","operands":[]}""") }

	shouldThrowAny { jsonEncoder.decodeFromString(NumberProvider.serializer(), """{"min":1,"max":2}""") }
}

class NumberProviderTests : FunSpec({
	test("number providers") {
		dataPack("numberProviders") {
			pretty()
			numberProviderTests()
		}
	}
})
