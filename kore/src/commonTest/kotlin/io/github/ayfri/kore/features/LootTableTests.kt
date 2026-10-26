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
import io.github.ayfri.kore.features.itemmodifiers.functions.reference
import io.github.ayfri.kore.features.itemmodifiers.functions.setCount
import io.github.ayfri.kore.features.loottables.*
import io.github.ayfri.kore.features.loottables.entries.*
import io.github.ayfri.kore.features.slotsources.*
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.conditions.weatherCheck
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.reference
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.LootTables
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.arguments.tagged.ItemModifierTagArgument
import io.github.ayfri.kore.generated.arguments.tagged.LootTableTagArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.utils.pretty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

fun DataPack.lootTableTests() {
	val lootTable = lootTable("all_entry_types") {
		modifier {
			enchantRandomly {
				options = listOf(Enchantments.LOOTING)
			}
		}

		pool {
			rolls = constant(2)
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
						setCount(1) {
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
				setCount(1)
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
								"count": 1
							},
							"value": "minecraft:gameplay/piglin_bartering"
						},
						{
							"type": "minecraft:sequence",
							"children": [
								{
									"type": "minecraft:tag",
									"expand": true,
									"items": "#minecraft:boats"
								}
							]
						}
					],
					"modifier": {
						"type": "minecraft:set_count",
						"count": 1
					},
					"rolls": 2
				}
			]
		}
	""".trimIndent()

	load {
		loot(self(), lootTable)
	}

	lootTable("all_slot_sources_test") {
		pool {
			rolls = constant(1)

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
					"rolls": 1
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
			setCount(1) {
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
					modifier { setCount(2) }
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

					modifier { setCount(2) }

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
			rolls = constant(1)
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
					"rolls": 1
				}
			]
		}
	""".trimIndent()
	roundTrip(lootTables.last())

	lootTable("modifier_ids") {
		pool {
			rolls = constant(1)
			modifier { reference(ItemModifierTagArgument("buffs", "my_pack")) }

			entries {
				item(Items.DIAMOND) {
					modifier { reference(ItemModifierArgument("shiny", "my_pack")) }
				}

				item(Items.GOLD_INGOT) {
					modifier {
						reference(ItemModifierArgument("shiny", "my_pack"))
						setCount(2)
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
							"type": "minecraft:item",
							"name": "minecraft:diamond",
							"modifier": "my_pack:shiny"
						},
						{
							"type": "minecraft:item",
							"name": "minecraft:gold_ingot",
							"modifier": [
								"my_pack:shiny",
								{
									"type": "minecraft:set_count",
									"count": 2
								}
							]
						}
					],
					"modifier": "#my_pack:buffs",
					"rolls": 1
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

	test("archaeology loot context is written with the game's spelling") {
		dataPack("lootTable") {
			pretty()

			lootTable("archaeology") {
				type = LootTableType.ARCHAEOLOGY
			}

			lootTables.last() assertsIs """
				{
					"type": "archaeology"
				}
			""".trimIndent()
		}
	}

	test("new loot context types are written with the game's names") {
		dataPack("lootTable") {
			pretty()

			listOf(
				LootTableType.CONTAINER_PROCESS to "container_process",
				LootTableType.ENCHANTED_DAMAGE to "enchanted_damage",
				LootTableType.ENCHANTED_ENTITY to "enchanted_entity",
				LootTableType.ENCHANTED_ITEM to "enchanted_item",
				LootTableType.ENCHANTED_LOCATION to "enchanted_location",
				LootTableType.HIT_BLOCK to "hit_block",
			).forEach { (contextType, name) ->
				lootTable("context_$name") {
					type = contextType
				}

				lootTables.last() assertsIs """
					{
						"type": "$name"
					}
				""".trimIndent()
				roundTrip(lootTables.last())
			}
		}
	}

	test("loot table entry with loot table IDs") {
		dataPack("lootTable") {
			pretty()

			lootTable("reward") {
				type = LootTableType.CHEST

				pool {
					entries {
						lootTable(LootTables.Chests.TrialChambers.REWARD_RARE) { weight = 8 }
						lootTable(LootTables.Chests.TrialChambers.REWARD_COMMON) { weight = 2 }
						lootTable(LootTables.Equipment.TRIAL_CHAMBER, LootTables.Equipment.TRIAL_CHAMBER_MELEE)
					}
				}
			}

			lootTables.last() assertsIs """
				{
					"pools": [
						{
							"entries": [
								{
									"type": "minecraft:loot_table",
									"value": "minecraft:chests/trial_chambers/reward_rare",
									"weight": 8
								},
								{
									"type": "minecraft:loot_table",
									"value": "minecraft:chests/trial_chambers/reward_common",
									"weight": 2
								},
								{
									"type": "minecraft:loot_table",
									"value": [
										"minecraft:equipment/trial_chamber",
										"minecraft:equipment/trial_chamber_melee"
									]
								}
							],
							"rolls": 1
						}
					],
					"type": "chest"
				}
			""".trimIndent()
			roundTrip(lootTables.last())
		}
	}

	test("loot table entry with a loot table tag and expand") {
		dataPack("lootTable") {
			pretty()

			lootTable("tagged") {
				pool {
					entries {
						lootTable(LootTableTagArgument("trial_loot", "my_pack"), expand = true)
					}
				}
			}

			lootTables.last() assertsIs """
				{
					"pools": [
						{
							"entries": [
								{
									"type": "minecraft:loot_table",
									"expand": true,
									"value": "#my_pack:trial_loot"
								}
							],
							"rolls": 1
						}
					]
				}
			""".trimIndent()
			roundTrip(lootTables.last())
		}
	}

	test("tag entry with a tag and with several items") {
		dataPack("lootTable") {
			pretty()

			lootTable("tags") {
				pool {
					entries {
						tag(Tags.Item.CREEPER_DROP_MUSIC_DISCS, expand = true)
						tag(Items.APPLE, Items.CARROT) { weight = 2 }
						tag(Tags.Item.ARROWS)
					}
				}
			}

			lootTables.last() assertsIs """
				{
					"pools": [
						{
							"entries": [
								{
									"type": "minecraft:tag",
									"expand": true,
									"items": "#minecraft:creeper_drop_music_discs"
								},
								{
									"type": "minecraft:tag",
									"items": [
										"minecraft:apple",
										"minecraft:carrot"
									],
									"weight": 2
								},
								{
									"type": "minecraft:tag",
									"items": "#minecraft:arrows"
								}
							],
							"rolls": 1
						}
					]
				}
			""".trimIndent()
			roundTrip(lootTables.last())
		}
	}

	test("pool rolls accept a context int provider ID") {
		dataPack("lootTable") {
			pretty()

			lootTable("rolls_reference") {
				pool {
					rolls = reference(ContextIntProviderArgument("rolls", "my_pack"))
				}
			}

			lootTables.last() assertsIs """
				{
					"pools": [
						{
							"entries": [],
							"rolls": "my_pack:rolls"
						}
					]
				}
			""".trimIndent()
			roundTrip(lootTables.last())
		}
	}

	test("rolls are an int provider and bonus rolls a float provider") {
		dataPack("lootTable") {
			pretty()

			lootTable("rolls_builders") {
				pool {
					rolls(2)
					bonusRolls(1f)
				}
			}

			lootTables.last() assertsIs """
				{
					"pools": [
						{
							"bonus_rolls": 1.0,
							"entries": [],
							"rolls": 2
						}
					]
				}
			""".trimIndent()
			roundTrip(lootTables.last())
		}
	}

	test("pool takes a plain number of rolls and keeps one roll by default") {
		dataPack("lootTable") {
			pretty()

			lootTable("pool_rolls") {
				pool(2) {
					bonusRolls(1f)
				}

				pool {}
			}

			lootTables.last() assertsIs """
				{
					"pools": [
						{
							"bonus_rolls": 1.0,
							"entries": [],
							"rolls": 2
						},
						{
							"entries": [],
							"rolls": 1
						}
					]
				}
			""".trimIndent()
			roundTrip(lootTables.last())
		}
	}

	test("rolls and bonus rolls draw uniformly from a range, equal ends being a constant") {
		dataPack("lootTable") {
			pretty()

			lootTable("pool_ranges") {
				pool(1..3) {
					bonusRolls(0f..1f)
				}

				pool {
					rolls(2..2)
				}

				pool {
					rolls(4..6)
				}
			}

			lootTables.last() assertsIs """
				{
					"pools": [
						{
							"bonus_rolls": {
								"type": "minecraft:uniform",
								"max": 1.0,
								"min": 0.0
							},
							"entries": [],
							"rolls": {
								"type": "minecraft:uniform",
								"max": 3,
								"min": 1
							}
						},
						{
							"entries": [],
							"rolls": 2
						},
						{
							"entries": [],
							"rolls": {
								"type": "minecraft:uniform",
								"max": 6,
								"min": 4
							}
						}
					]
				}
			""".trimIndent()
			roundTrip(lootTables.last())
		}
	}

	test("an empty range of rolls throws") {
		dataPack("lootTable") {
			shouldThrow<IllegalArgumentException> {
				lootTable("empty_rolls") { pool { rolls(3..1) } }
			}.message assertsIs "Cannot draw from the empty range 3..1."

			shouldThrow<IllegalArgumentException> {
				lootTable("empty_bonus_rolls") { pool { bonusRolls(1f..0f) } }
			}
		}
	}

	test("item entry modifier stays on the entry") {
		dataPack("lootTable") {
			pretty()

			lootTable("nested_modifier") {
				pool {
					entries {
						item(Items.DIAMOND) {
							modifier { setCount(2) }
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
										"count": 2
									}
								}
							],
							"rolls": 1
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
				modifier { setCount(2) }
			}

			lootTables.last() assertsIs """
				{
					"modifier": {
						"type": "minecraft:set_count",
						"count": 2
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
							"rolls": 1
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
							"rolls": 1
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
						setCount(2)
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
									"count": 2
								},
								{
									"type": "minecraft:explosion_decay"
								}
							],
							"rolls": 1
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

							modifier { setCount(2) }

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
										"count": 2
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
							"rolls": 1
						}
					]
				}
			""".trimIndent()
		}
	}
})
