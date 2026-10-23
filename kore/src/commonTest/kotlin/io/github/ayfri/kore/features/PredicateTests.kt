package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.components.entity.axolotlVariant
import io.github.ayfri.kore.arguments.components.item.damage
import io.github.ayfri.kore.arguments.components.item.unbreakable
import io.github.ayfri.kore.arguments.components.matchers.customData
import io.github.ayfri.kore.arguments.components.matchers.enchantmentPredicate
import io.github.ayfri.kore.arguments.components.matchers.enchantments
import io.github.ayfri.kore.arguments.enums.AxolotlVariants
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsThrows
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.*
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.features.predicates.providers.*
import io.github.ayfri.kore.features.predicates.sub.*
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.generated.arguments.tagged.PredicateTagArgument
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.MoonPhaseValue
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.beesStayInHive
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.moonPhase
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.set
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf

fun DataPack.predicateTests() {
	predicate("all_of") {
		allOf {
			enchantmentActiveCheck(false)
			randomChance(0.25f)
			timeCheck(WorldClocks.OVERWORLD, 10..20)
			weatherCheck(raining = false, thundering = true)
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:all_of",
			"terms": [
				{
					"type": "minecraft:enchantment_active_check",
					"active": false
				},
				{
					"type": "minecraft:random_chance",
					"chance": 0.25
				},
				{
					"type": "minecraft:time_check",
					"clock": "minecraft:overworld",
					"value": {
						"max": 20,
						"min": 10
					}
				},
				{
					"type": "minecraft:weather_check",
					"raining": false,
					"thundering": true
				}
			]
		}
	""".trimIndent()

	predicate("any_of") {
		anyOf {
			killedByPlayer(true)
			survivesExplosion()
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:any_of",
			"terms": [
				{
					"type": "minecraft:killed_by_player",
					"inverse": true
				},
				{
					"type": "minecraft:survives_explosion"
				}
			]
		}
	""".trimIndent()

	predicate("match_block") {
		matchBlock(Blocks.WHEAT) { state("age", "7") }
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:match_block",
			"blocks": "minecraft:wheat",
			"state": {
				"age": "7"
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("match_block_list") {
		matchBlock(Blocks.SMOKER, Blocks.BLAST_FURNACE)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:match_block",
			"blocks": [
				"minecraft:smoker",
				"minecraft:blast_furnace"
			]
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("match_block_full") {
		matchBlock(Tags.Block.LOGS) {
			nbt {
				this["foo"] = "bar"
			}
			states {
				this["axis"] = "y"
				this["lit"] = "true"
			}
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:match_block",
			"blocks": "#minecraft:logs",
			"nbt": {
				"foo": "bar"
			},
			"state": {
				"axis": "y",
				"lit": "true"
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("match_block_empty") {
		matchBlock()
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:match_block"
		}
	""".trimIndent()

	predicate("damage_source_properties") {
		damageSourceProperties {
			isDirect = true
			directEntity {
				entityType(EntityTypes.ZOMBIE)
			}
			sourceEntity {
				entityType(EntityTypes.SKELETON)
			}
			tag(Tags.DamageType.IS_PROJECTILE, expected = true)
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:damage_source_properties",
			"predicate": {
				"direct_entity": {
					"minecraft:entity_type": "minecraft:zombie"
				},
				"is_direct": true,
				"source_entity": {
					"minecraft:entity_type": "minecraft:skeleton"
				},
				"tags": [
					{
						"expected": true,
						"id": "#minecraft:is_projectile"
					}
				]
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("damage_source_tags") {
		damageSourceProperties {
			tag(Tags.DamageType.IS_FIRE)
			tag(Tags.DamageType.BYPASSES_INVULNERABILITY, expected = false)
			tag(DamageTypes.LAVA, DamageTypes.IN_FIRE)
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:damage_source_properties",
			"predicate": {
				"tags": [
					{
						"expected": true,
						"id": "#minecraft:is_fire"
					},
					{
						"expected": false,
						"id": "#minecraft:bypasses_invulnerability"
					},
					{
						"expected": true,
						"id": [
							"minecraft:lava",
							"minecraft:in_fire"
						]
					}
				]
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("enchantment_active_check") {
		enchantmentActiveCheck(true)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:enchantment_active_check",
			"active": true
		}
	""".trimIndent()

	predicate("entity_properties") {
		entityProperties {
			components {
				axolotlVariant(AxolotlVariants.CYAN)
				damage(12)
				!unbreakable()
			}

			effects {
				this[Effects.INVISIBILITY] = mobEffectPredicate {
					amplifier = rangeOrInt(1)
					ambient = true
					visible = false
				}
			}

			equipment {
				mainHand = itemStackPredicate(Items.DIAMOND_SWORD)
			}

			flags {
				isBaby = true
			}

			location {
				block {
					blocks(Blocks.STONE)
				}
			}

			movement {
				x(1.0, 4.0)
				horizontalSpeed(1.0)
			}

			movementAffectedBy {
				canSeeSky = true
			}

			nbt {
				this["foo"] = "bar"
			}

			passenger {
				team("foo")
			}

			periodicTick(20)

			predicates {
				customData {
					this["foo"] = "bar"
				}
			}

			slots {
				this[WEAPON.MAINHAND] = itemStackPredicate(Items.DIAMOND_SWORD)
			}

			steppingOn {
				block {
					blocks(Blocks.STONE)
					components {
						damage(5)
					}
					predicates {
						customData {
							this["foo"] = "bar"
						}
					}
					state("up", "bottom")
				}
			}

			entityTags {
				allOf("boss")
			}

			team("alpha")
			entityType(EntityTypes.MARKER)

			typeSpecific {
				player {
					gamemodes(Gamemode.SURVIVAL)
				}
			}

			vehicle {
				distance {
					x(1f..4f)
					z(1f)
				}
			}
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:components": {
					"axolotl/variant": "cyan",
					"damage": 12,
					"!unbreakable": {}
				},
				"minecraft:effects": {
					"minecraft:invisibility": {
						"ambient": true,
						"amplifier": 1,
						"visible": false
					}
				},
				"minecraft:equipment": {
					"mainhand": {
						"items": "minecraft:diamond_sword"
					}
				},
				"minecraft:flags": {
					"is_baby": true
				},
				"minecraft:location": {
					"block": {
						"blocks": "minecraft:stone"
					}
				},
				"minecraft:movement": {
					"x": {
						"min": 1.0,
						"max": 4.0
					},
					"horizontal_speed": 1.0
				},
				"minecraft:movement_affected_by": {
					"can_see_sky": true
				},
				"minecraft:nbt": {
					"foo": "bar"
				},
				"minecraft:passenger": {
					"minecraft:team": "foo"
				},
				"minecraft:periodic_tick": 20,
				"minecraft:predicates": {
					"minecraft:custom_data": {
						"foo": "bar"
					}
				},
				"minecraft:slots": {
					"weapon.mainhand": {
						"items": "minecraft:diamond_sword"
					}
				},
				"minecraft:stepping_on": {
					"block": {
						"blocks": "minecraft:stone",
						"components": {
							"damage": 5
						},
						"predicates": {
							"minecraft:custom_data": {
								"foo": "bar"
							}
						},
						"state": {
							"up": "bottom"
						}
					}
				},
				"minecraft:entity_tags": {
					"all_of": [
						"boss"
					]
				},
				"minecraft:team": "alpha",
				"minecraft:entity_type": "minecraft:marker",
				"minecraft:type_specific/player": {
					"gamemode": [
						"survival"
					]
				},
				"minecraft:vehicle": {
					"minecraft:distance": {
						"x": {
							"min": 1.0,
							"max": 4.0
						},
						"z": 1.0
					}
				}
			}
		}
	""".trimIndent()

	predicate("entity_score") {
		entityScores(EntityTarget.THIS) {
			this["kills"] = intRange(1, 5)
			this["deaths"] = intValue(2)
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:entity_scores",
			"entity": "this",
			"scores": {
				"kills": {
					"max": 5,
					"min": 1
				},
				"deaths": 2
			}
		}
	""".trimIndent()

	predicate("environment_attribute_check_boolean") {
		environmentAttributeCheck(EnvironmentAttributes.Gameplay.BEES_STAY_IN_HIVE, true)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:environment_attribute_check",
			"attribute": "minecraft:gameplay/bees_stay_in_hive",
			"value": true
		}
	""".trimIndent()

	predicate("environment_attribute_check_float") {
		environmentAttributeCheck(EnvironmentAttributes.Visual.SKY_LIGHT_FACTOR, 0.5f)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:environment_attribute_check",
			"attribute": "minecraft:visual/sky_light_factor",
			"value": 0.5
		}
	""".trimIndent()

	predicate("environment_attribute_check_moon_phase") {
		environmentAttributeCheck(
			EnvironmentAttributes.Visual.MOON_PHASE,
			MoonPhaseValue(Textures.Environment.Celestial.Moon.FULL_MOON)
		)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:environment_attribute_check",
			"attribute": "minecraft:visual/moon_phase",
			"value": "minecraft:environment/celestial/moon/full_moon"
		}
	""".trimIndent()

	predicate("environment_attribute_check_scope") {
		environmentAttributeCheck {
			moonPhase(Textures.Environment.Celestial.Moon.FULL_MOON)
			beesStayInHive(true)
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:all_of",
			"terms": [
				{
					"type": "minecraft:environment_attribute_check",
					"attribute": "minecraft:visual/moon_phase",
					"value": "minecraft:environment/celestial/moon/full_moon"
				},
				{
					"type": "minecraft:environment_attribute_check",
					"attribute": "minecraft:gameplay/bees_stay_in_hive",
					"value": true
				}
			]
		}
	""".trimIndent()

	predicate("inverted") {
		inverted {
			randomChance(0.1f)
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:inverted",
			"term": {
				"type": "minecraft:random_chance",
				"chance": 0.1
			}
		}
	""".trimIndent()

	predicate("inverted_condition") {
		inverted(SurvivesExplosion)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:inverted",
			"term": {
				"type": "minecraft:survives_explosion"
			}
		}
	""".trimIndent()

	predicate("killed_by_player") {
		killedByPlayer(true)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:killed_by_player",
			"inverse": true
		}
	""".trimIndent()

	predicate("location_check") {
		locationCheck(offsetX = 1, offsetY = 2, offsetZ = 3) {
			biomes(Biomes.PLAINS, Biomes.SAVANNA)
			block {
				blocks(Blocks.GOLD_BLOCK)
				states {
					this["lit"] = "true"
				}
			}
			canSeeSky = true
			dimension = Dimensions.OVERWORLD
			fluids(Fluids.WATER) {
				states {
					this["level"] = "0"
				}
			}
			light(7)
			position {
				x(1)
				y(2..4)
				z(3)
			}
			smokey = true
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:location_check",
			"offsetX": 1,
			"offsetY": 2,
			"offsetZ": 3,
			"predicate": {
				"biomes": [
					"minecraft:plains",
					"minecraft:savanna"
				],
				"block": {
					"blocks": "minecraft:gold_block",
					"state": {
						"lit": "true"
					}
				},
				"can_see_sky": true,
				"dimension": "minecraft:overworld",
				"fluid": {
					"fluids": "minecraft:water",
					"state": {
						"level": "0"
					}
				},
				"light": {
					"light": 7
				},
				"position": {
					"x": 1.0,
					"y": {
						"min": 2.0,
						"max": 4.0
					},
					"z": 3.0
				},
				"smokey": true
			}
		}
	""".trimIndent()

	predicate("match_tool") {
		matchTool {
			items(Items.DIAMOND_PICKAXE, Items.IRON_PICKAXE)
			components {
				damage(5)
				unbreakable()
			}
			predicates {
				enchantments(
					enchantmentPredicate(Enchantments.EFFICIENCY, levels = rangeOrInt(3)),
					enchantmentPredicate(Enchantments.UNBREAKING, levels = rangeOrInt(2))
				)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:match_tool",
			"predicate": {
				"items": [
					"minecraft:diamond_pickaxe",
					"minecraft:iron_pickaxe"
				],
				"components": {
					"damage": 5,
					"unbreakable": {}
				},
				"predicates": {
					"minecraft:enchantments": [
						{
							"enchantments": "minecraft:efficiency",
							"levels": 3
						},
						{
							"enchantments": "minecraft:unbreaking",
							"levels": 2
						}
					]
				}
			}
		}
	""".trimIndent()

	predicate("match_tool_items") {
		matchTool(Items.DIAMOND_PICKAXE, Items.IRON_PICKAXE)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:match_tool",
			"predicate": {
				"items": [
					"minecraft:diamond_pickaxe",
					"minecraft:iron_pickaxe"
				]
			}
		}
	""".trimIndent()

	predicate("random_chance") {
		randomChance(0.4f)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:random_chance",
			"chance": 0.4
		}
	""".trimIndent()

	predicate("random_chance_with_enchanted_bonus") {
		randomChanceWithEnchantedBonus(unenchantedChance = 1f, enchantedChance = 2, Enchantments.FORTUNE)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:random_chance_with_enchanted_bonus",
			"unenchanted_chance": 1.0,
			"enchanted_chance": 2,
			"enchantment": "minecraft:fortune"
		}
	""".trimIndent()

	predicate("reference_all_of") {
		allOf {
			reference("kore:test")
			reference(PredicateArgument("other", "kore"))
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:all_of",
			"terms": [
				"kore:test",
				"kore:other"
			]
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("reference_any_of_tag") {
		anyOf { reference(PredicateTagArgument("night_like", "kore")) }
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:any_of",
			"terms": "#kore:night_like"
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("reference_inverted") {
		inverted(PredicateArgument("test", "kore"))
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:inverted",
			"term": "kore:test"
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("reference_mixed") {
		reference(PredicateArgument("a", "my_pack"))
		randomChance(0.5f)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:all_of",
			"terms": [
				"my_pack:a",
				{
					"type": "minecraft:random_chance",
					"chance": 0.5
				}
			]
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("reference_mixed_any_of") {
		anyOf {
			randomChance(0.5f)
			reference("my_pack:a")
			reference(PredicateTagArgument("t", "my_pack"))
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:any_of",
			"terms": [
				{
					"type": "minecraft:random_chance",
					"chance": 0.5
				},
				"my_pack:a",
				"#my_pack:t"
			]
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("only_reference") {
		reference("kore:test")
	}

	assertsThrows("Predicate file 'only_reference' must hold an inline condition, not a reference.") {
		predicates.last().generateJson(this)
	}

	predicate("survives_explosion") {
		survivesExplosion()
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:survives_explosion"
		}
	""".trimIndent()

	predicate("table_bonus") {
		tableBonus(Enchantments.LOOTING, 0.1f, 0.25f, 0.5f)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:table_bonus",
			"enchantment": "minecraft:looting",
			"chances": [
				0.1,
				0.25,
				0.5
			]
		}
	""".trimIndent()

	predicate("table_bonus_builder") {
		tableBonus(Enchantments.FORTUNE) {
			add(0.1f)
			add(0.2f)
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:table_bonus",
			"enchantment": "minecraft:fortune",
			"chances": [
				0.1,
				0.2
			]
		}
	""".trimIndent()

	predicate("time_check") {
		timeCheck(WorldClocks.OVERWORLD, 5..15)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:time_check",
			"clock": "minecraft:overworld",
			"value": {
				"max": 15,
				"min": 5
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("time_check_value") {
		timeCheck(WorldClocks.OVERWORLD, 1000)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:time_check",
			"clock": "minecraft:overworld",
			"value": 1000
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("time_check_with_period") {
		timeCheck(WorldClocks.THE_END, 0..6000, period = 24000)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:time_check",
			"clock": "minecraft:the_end",
			"value": {
				"max": 6000,
				"min": 0
			},
			"period": 24000
		}
	""".trimIndent()

	predicate("int_value_check") {
		intValueCheck(score("kills", EntityTarget.THIS), 0..10)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:int_value_check",
			"test": {
				"max": 10,
				"min": 0
			},
			"value": {
				"type": "minecraft:score",
				"score": "kills",
				"target": {
					"type": "minecraft:context",
					"target": "this"
				}
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("int_value_check_exact") {
		intValueCheck(score("kills", EntityTarget.THIS), intValue(3))
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:int_value_check",
			"test": 3,
			"value": {
				"type": "minecraft:score",
				"score": "kills",
				"target": {
					"type": "minecraft:context",
					"target": "this"
				}
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("int_value_check_open_bound") {
		intValueCheck(score("kills", EntityTarget.THIS), min = constant(1))
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:int_value_check",
			"test": {
				"min": 1
			},
			"value": {
				"type": "minecraft:score",
				"score": "kills",
				"target": {
					"type": "minecraft:context",
					"target": "this"
				}
			}
		}
	""".trimIndent()

	predicate("int_value_check_add") {
		intValueCheck(add(constant(1), constant(2), uniform(3, 5)), 0..10)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:int_value_check",
			"test": {
				"max": 10,
				"min": 0
			},
			"value": {
				"type": "minecraft:add",
				"inputs": [
					1,
					2,
					{
						"type": "minecraft:uniform",
						"max": 5,
						"min": 3
					}
				]
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("float_value_check") {
		floatValueCheck(uniform(0f, 1f), 0f..1f)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:float_value_check",
			"test": {
				"max": 1.0,
				"min": 0.0
			},
			"value": {
				"type": "minecraft:uniform",
				"max": 1.0,
				"min": 0.0
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("float_value_check_exact") {
		floatValueCheck(constant(0.5f), floatValue(0.5f))
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:float_value_check",
			"test": 0.5,
			"value": 0.5
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("weather_check") {
		weatherCheck(raining = true, thundering = false)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:weather_check",
			"raining": true,
			"thundering": false
		}
	""".trimIndent()

	predicate("round_trip_random_chance") {
		randomChance(0.4f)
	}
	roundTrip(predicates.last())

	predicate("one_condition") {
		randomChance(0.5f)
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:random_chance",
			"chance": 0.5
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("two_conditions") {
		randomChance(0.5f)
		killedByPlayer()
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:all_of",
			"terms": [
				{
					"type": "minecraft:random_chance",
					"chance": 0.5
				},
				{
					"type": "minecraft:killed_by_player"
				}
			]
		}
	""".trimIndent()
	roundTrip(predicates.last())

	val chance = ContextFloatProviderArgument("drop_chance", "my_pack")

	predicate("random_chance_reference") {
		randomChance(reference(chance))
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:random_chance",
			"chance": "my_pack:drop_chance"
		}
	""".trimIndent()
	roundTrip(predicates.last())

	val bonus = ContextIntProviderArgument("bonus", "my_pack")

	predicate("int_value_check_reference") {
		intValueCheck(reference(bonus), intRange(reference(bonus), constant(10)))
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:int_value_check",
			"test": {
				"max": 10,
				"min": "my_pack:bonus"
			},
			"value": "my_pack:bonus"
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("float_value_check_reference") {
		floatValueCheck(reference(chance), min = reference(chance))
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:float_value_check",
			"test": {
				"min": "my_pack:drop_chance"
			},
			"value": "my_pack:drop_chance"
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("time_check_reference") {
		timeCheck(WorldClocks.OVERWORLD, reference(bonus), constant(6000))
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:time_check",
			"clock": "minecraft:overworld",
			"value": {
				"max": 6000,
				"min": "my_pack:bonus"
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("entity_scores_reference") {
		entityScores(EntityTarget.THIS) {
			this["kills"] = intRange(reference(bonus), reference(ContextIntProviders.Cooking.TIME_COAL))
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:entity_scores",
			"entity": "this",
			"scores": {
				"kills": {
					"max": "minecraft:cooking/time_coal",
					"min": "my_pack:bonus"
				}
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("entity_scores_value") {
		entityScores(EntityTarget.THIS) {
			this["kills"] = intValue(reference(bonus))
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:entity_scores",
			"entity": "this",
			"scores": {
				"kills": "my_pack:bonus"
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	jsonEncoder.decodeFromString(ContextIntProviderRange.serializer(), """{"min":1,"max":2}""")
		.bounds.shouldBeInstanceOf<ContextIntProviderBounds>()
	jsonEncoder.decodeFromString(ContextIntProviderRange.serializer(), """{"type":"minecraft:uniform","min":1,"max":2}""")
		.value.shouldBeInstanceOf<UniformContextIntProvider>()
	jsonEncoder.decodeFromString(ContextIntProviderRange.serializer(), "3").value assertsIs ConstantContextIntProvider(3)

	predicate("no_condition") {}

	predicates.last() assertsIs """
		{
			"type": "minecraft:all_of",
			"terms": []
		}
	""".trimIndent()

	predicate("all_of_single_term") {
		allOf {
			killedByPlayer()
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:all_of",
			"terms": {
				"type": "minecraft:killed_by_player"
			}
		}
	""".trimIndent()
	roundTrip(predicates.last())

	predicate("any_of_single_term") {
		anyOf {
			survivesExplosion()
		}
	}

	predicates.last() assertsIs """
		{
			"type": "minecraft:any_of",
			"terms": {
				"type": "minecraft:survives_explosion"
			}
		}
	""".trimIndent()

	jsonEncoder.decodeFromString(
		Predicate.serializer(),
		"""{"type":"minecraft:all_of","terms":{"type":"minecraft:killed_by_player"}}""",
	).predicateConditions assertsIs listOf(AllOf(listOf(KilledByPlayer())))

	val id = jsonEncoder.decodeFromString(PredicateCondition.serializer(), "\"my_pack:a\"")
		.shouldBeInstanceOf<PredicateReference>().id
	id.shouldBeInstanceOf<PredicateArgument>().asString() assertsIs "my_pack:a"

	val tag = jsonEncoder.decodeFromString(PredicateCondition.serializer(), "\"#my_pack:t\"")
		.shouldBeInstanceOf<PredicateReference>().id
	tag.shouldBeInstanceOf<PredicateTagArgument>().asString() assertsIs "#my_pack:t"

	shouldThrowAny { jsonEncoder.decodeFromString(PredicateCondition.serializer(), "5.0") }
	shouldThrowAny { jsonEncoder.decodeFromString(PredicateCondition.serializer(), """{"type":"minecraft:reference","name":"my_pack:a"}""") }
}

class PredicateTests : FunSpec({
	test("predicate") {
		dataPack("predicate") {
			pretty()
			predicateTests()
		}
	}
})
