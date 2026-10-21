package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.HOTBAR
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.loot
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.itemmodifiers.functions.condition
import io.github.ayfri.kore.features.itemmodifiers.functions.enchantRandomly
import io.github.ayfri.kore.features.itemmodifiers.functions.explosionDecay
import io.github.ayfri.kore.features.itemmodifiers.functions.setCount
import io.github.ayfri.kore.features.loottables.*
import io.github.ayfri.kore.features.loottables.entries.*
import io.github.ayfri.kore.features.slotsources.*
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.conditions.weatherCheck
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.LootTables
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.lootTableTests() {
	val lootTable = lootTable("all_entry_types") {
		modifier {
			enchantRandomly {
				options = listOf(Enchantments.LOOTING)
			}
		}

		pool {
			rolls = constant(2f)
			bonusRolls = constant(1f)
			condition {
				weatherCheck(true)
			}

			entries {
				alternatives {
					children {
						empty(quality = 2, weight = 3)
					}
				}

				dynamic(LootEntryDynamicName.SHERDS)

				group(group = {
					children {
						item(Items.DIAMOND)
					}
				})

				lootTable(LootTables.Gameplay.PIGLIN_BARTERING) {
					condition {
						weatherCheck(true)
					}

					modifier {
						setCount(1f) {
							condition {
								randomChance(0.5f)
							}
						}
					}
				}

				sequence(sequence = {
					children {
						tag(Tags.Item.BOATS, expand = true)
					}
				})
			}

			modifier {
				setCount(1f)
			}
		}
	}

	lootTables.last() assertsIs """
		{
			"modifier": {
				"type": "minecraft:enchant_randomly",
				"options": "minecraft:looting"
			},
			"pools": [
				{
					"bonus_rolls": 1.0,
					"condition": {
						"type": "minecraft:weather_check",
						"raining": true
					},
					"entries": [
						{
							"type": "minecraft:alternatives",
							"children": [
								{
									"type": "minecraft:empty",
									"quality": 2,
									"weight": 3
								}
							]
						},
						{
							"type": "minecraft:dynamic",
							"name": "minecraft:sherds"
						},
						{
							"type": "minecraft:group",
							"children": [
								{
									"type": "minecraft:item",
									"name": "minecraft:diamond"
								}
							]
						},
						{
							"type": "minecraft:loot_table",
							"value": "minecraft:gameplay/piglin_bartering",
							"condition": {
								"type": "minecraft:weather_check",
								"raining": true
							},
							"modifier": {
								"type": "minecraft:set_count",
								"condition": {
									"type": "minecraft:random_chance",
									"chance": 0.5
								},
								"count": 1.0
							}
						},
						{
							"type": "minecraft:sequence",
							"children": [
								{
									"type": "minecraft:tag",
									"expand": true,
									"name": "minecraft:boats"
								}
							]
						}
					],
					"modifier": {
						"type": "minecraft:set_count",
						"count": 1.0
					},
					"rolls": 2.0
				}
			]
		}
	""".trimIndent()

	load {
		loot(self(), lootTable)
	}

	lootTable("all_slot_sources_test") {
		pool {
			rolls = constant(1f)

			entries {
				slots {
					slotSources {

						contents(InventoryComponentType.CONTAINER) {
							slotSource {
								slotRange(SlotSourceOrigin.BLOCK_ENTITY, "container.*")
							}
						}

						empty()

						filtered {
							itemFilter {
								count = rangeOrInt(1..16)
							}

							slotSource {
								slotRange(SlotSourceOrigin.TARGET_ENTITY, HOTBAR.all())
							}
						}

						group {
							empty()
							slotRange(SlotSourceOrigin.THIS, HOTBAR.all())
						}

						limitSlots(5) {
							slotSource {
								slotRange(SlotSourceOrigin.THIS, HOTBAR.all())
							}
						}

						slotRange(SlotSourceOrigin.THIS, HOTBAR)
					}
				}
			}
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"entries": [
						{
							"type": "minecraft:slots",
							"slot_source": [
								{
									"type": "minecraft:contents",
									"component": "minecraft:container",
									"slot_source": {
										"type": "minecraft:slot_range",
										"source": "block_entity",
										"slots": "container.*"
									}
								},
								{
									"type": "minecraft:empty"
								},
								{
									"type": "minecraft:filtered",
									"item_filter": {
										"count": {
											"min": 1,
											"max": 16
										}
									},
									"slot_source": {
										"type": "minecraft:slot_range",
										"source": "target_entity",
										"slots": "hotbar.*"
									}
								},
								{
									"type": "minecraft:group",
									"terms": [
										{
											"type": "minecraft:empty"
										},
										{
											"type": "minecraft:slot_range",
											"source": "this",
											"slots": "hotbar.*"
										}
									]
								},
								{
									"type": "minecraft:limit_slots",
									"limit": 5,
									"slot_source": {
										"type": "minecraft:slot_range",
										"source": "this",
										"slots": "hotbar.*"
									}
								},
								{
									"type": "minecraft:slot_range",
									"source": "this",
									"slots": "hotbar.*"
								}
							]
						}
					],
					"rolls": 1.0
				}
			]
		}
	""".trimIndent()

	lootTable("round_trip_simple_pool") {
		pool {
			entries {
				item(Items.DIAMOND)
			}
		}
	}
	roundTrip(lootTables.last())

	lootTable("round_trip_modifiers") {
		modifier {
			setCount(1f) {
				condition {
					randomChance(0.5f)
					weatherCheck(true)
				}
			}
			explosionDecay()
		}

		pool {
			entries {
				item(Items.DIAMOND) {
					modifier { setCount(2f) }
				}
			}
		}
	}
	roundTrip(lootTables.last())

	lootTable("round_trip_conditions") {
		pool {
			condition {
				randomChance(0.5f)
				weatherCheck(true)
			}

			entries {
				group(group = {
					condition {
						randomChance(0.5f)
						weatherCheck(true)
					}

					modifier { setCount(2f) }

					children {
						item(Items.DIAMOND)
					}
				})
			}
		}
	}
	roundTrip(lootTables.last())

	lootTable("predicate_ids") {
		pool {
			rolls = constant(1f)
			condition(PredicateArgument("is_night", "my_pack"))

			entries {
				item(Items.DIAMOND) {
					condition(PredicateArgument("lucky", "my_pack"))
				}
			}
		}
	}

	lootTables.last() assertsIs """
		{
			"pools": [
				{
					"condition": "my_pack:is_night",
					"entries": [
						{
							"type": "minecraft:item",
							"name": "minecraft:diamond",
							"condition": "my_pack:lucky"
						}
					],
					"rolls": 1.0
				}
			]
		}
	""".trimIndent()
	roundTrip(lootTables.last())
}

class LootTableTests : FunSpec({
	test("loot table") {
		dataPack("lootTable") {
			pretty()
			lootTableTests()
		}
	}

	test("item entry modifier stays on the entry") {
		dataPack("lootTable") {
			pretty()

			lootTable("nested_modifier") {
				pool {
					entries {
						item(Items.DIAMOND) {
							modifier { setCount(2f) }
						}
					}
				}
			}

			lootTables.last() assertsIs """
				{
					"pools": [
						{
							"entries": [
								{
									"type": "minecraft:item",
									"name": "minecraft:diamond",
									"modifier": {
										"type": "minecraft:set_count",
										"count": 2.0
									}
								}
							],
							"rolls": 1.0
						}
					]
				}
			""".trimIndent()
		}
	}

	test("table modifier with a single function is written bare") {
		dataPack("lootTable") {
			pretty()

			lootTable("table_modifier") {
				modifier { setCount(2f) }
			}

			lootTables.last() assertsIs """
				{
					"modifier": {
						"type": "minecraft:set_count",
						"count": 2.0
					}
				}
			""".trimIndent()
		}
	}

	test("pool condition is a single predicate and several become an all_of") {
		dataPack("lootTable") {
			pretty()

			lootTable("pool_conditions") {
				pool {
					condition { weatherCheck(true) }
				}

				pool {
					condition {
						randomChance(0.5f)
						weatherCheck(true)
					}
				}
			}

			lootTables.last() assertsIs """
				{
					"pools": [
						{
							"condition": {
								"type": "minecraft:weather_check",
								"raining": true
							},
							"entries": [],
							"rolls": 1.0
						},
						{
							"condition": {
								"type": "minecraft:all_of",
								"terms": [
									{
										"type": "minecraft:random_chance",
										"chance": 0.5
									},
									{
										"type": "minecraft:weather_check",
										"raining": true
									}
								]
							},
							"entries": [],
							"rolls": 1.0
						}
					]
				}
			""".trimIndent()
		}
	}

	test("pool with several modifier functions writes a list") {
		dataPack("lootTable") {
			pretty()

			lootTable("pool_modifier_list") {
				pool {
					modifier {
						setCount(2f)
						explosionDecay()
					}
				}
			}

			lootTables.last() assertsIs """
				{
					"pools": [
						{
							"entries": [],
							"modifier": [
								{
									"type": "minecraft:set_count",
									"count": 2.0
								},
								{
									"type": "minecraft:explosion_decay"
								}
							],
							"rolls": 1.0
						}
					]
				}
			""".trimIndent()
		}
	}

	test("composite entries take a condition and a modifier") {
		dataPack("lootTable") {
			pretty()

			lootTable("composite") {
				pool {
					entries {
						group(group = {
							condition {
								randomChance(0.5f)
								weatherCheck(true)
							}

							modifier { setCount(2f) }

							children {
								item(Items.DIAMOND)
							}
						})

						alternatives {
							modifier { explosionDecay() }

							children {
								empty()
							}
						}

						sequence(sequence = {
							condition { weatherCheck(true) }
						})
					}
				}
			}

			lootTables.last() assertsIs """
				{
					"pools": [
						{
							"entries": [
								{
									"type": "minecraft:group",
									"children": [
										{
											"type": "minecraft:item",
											"name": "minecraft:diamond"
										}
									],
									"condition": {
										"type": "minecraft:all_of",
										"terms": [
											{
												"type": "minecraft:random_chance",
												"chance": 0.5
											},
											{
												"type": "minecraft:weather_check",
												"raining": true
											}
										]
									},
									"modifier": {
										"type": "minecraft:set_count",
										"count": 2.0
									}
								},
								{
									"type": "minecraft:alternatives",
									"children": [
										{
											"type": "minecraft:empty",
											"quality": 1,
											"weight": 1
										}
									],
									"modifier": {
										"type": "minecraft:explosion_decay"
									}
								},
								{
									"type": "minecraft:sequence",
									"children": [],
									"condition": {
										"type": "minecraft:weather_check",
										"raining": true
									}
								}
							],
							"rolls": 1.0
						}
					]
				}
			""".trimIndent()
		}
	}
})
