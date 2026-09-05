package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.components.item.damage
import io.github.ayfri.kore.arguments.components.matchers.villagerVariant
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.conditions.entityProperties
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.features.predicates.sub.*
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.set
import io.kotest.core.spec.style.FunSpec

fun DataPack.predicateEntitySubPredicateTests() {
	predicate("components_sub") {
		entityProperties {
			components {
				damage(3)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:components": {
					"damage": 3
				}
			}
		}
	""".trimIndent()

	predicate("distance_sub") {
		entityProperties {
			distance {
				absolute(1.5)
				horizontal(0, 4)
				x(1..4)
				y(2)
				z(-1.0, 1.0)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:distance": {
					"absolute": 1.5,
					"horizontal": {
						"min": 0.0,
						"max": 4.0
					},
					"x": {
						"min": 1.0,
						"max": 4.0
					},
					"y": 2.0,
					"z": {
						"min": -1.0,
						"max": 1.0
					}
				}
			}
		}
	""".trimIndent()

	predicate("effects_sub") {
		entityProperties {
			effects(Effects.SPEED to mobEffectPredicate { amplifier = rangeOrInt(1) })
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:effects": {
					"minecraft:speed": {
						"amplifier": 1
					}
				}
			}
		}
	""".trimIndent()

	predicate("entity_tags_sub") {
		entityProperties {
			entityTags {
				allOf("a")
				anyOf("b")
				noneOf("c")
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:entity_tags": {
					"all_of": [
						"a"
					],
					"any_of": [
						"b"
					],
					"none_of": [
						"c"
					]
				}
			}
		}
	""".trimIndent()

	predicate("entity_type_sub") {
		entityProperties {
			entityType(EntityTypes.PIG, EntityTypes.COW)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:entity_type": [
					"minecraft:pig",
					"minecraft:cow"
				]
			}
		}
	""".trimIndent()

	predicate("equipment_sub") {
		entityProperties {
			equipment {
				body = itemStackPredicate(Items.SADDLE)
				saddle = itemStackPredicate(Items.SADDLE)
				mainHand = itemStackPredicate(Items.STICK) { count(2) }
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:equipment": {
					"body": {
						"items": "minecraft:saddle"
					},
					"mainhand": {
						"count": 2,
						"items": "minecraft:stick"
					},
					"saddle": {
						"items": "minecraft:saddle"
					}
				}
			}
		}
	""".trimIndent()

	predicate("flags_sub") {
		entityProperties {
			flags {
				isBaby = true
				isFlying = false
				isInWater = true
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:flags": {
					"is_baby": true,
					"is_flying": false,
					"is_in_water": true
				}
			}
		}
	""".trimIndent()

	predicate("location_sub") {
		entityProperties {
			location {
				biomes(Biomes.PLAINS)
				block(Blocks.STONE)
				canSeeSky = true
				dimension = Dimensions.OVERWORLD
				fluids(Fluids.WATER)
				light(3, 7)
				position {
					x(1)
					y(-64..320)
					z(2.5)
				}
				smokey = false
				structures(ConfiguredStructures.VILLAGE_PLAINS)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:location": {
					"biomes": "minecraft:plains",
					"block": {
						"blocks": "minecraft:stone"
					},
					"can_see_sky": true,
					"dimension": "minecraft:overworld",
					"fluid": {
						"fluids": "minecraft:water"
					},
					"light": {
						"light": {
							"min": 3,
							"max": 7
						}
					},
					"position": {
						"x": 1.0,
						"y": {
							"min": -64.0,
							"max": 320.0
						},
						"z": 2.5
					},
					"smokey": false,
					"structures": "minecraft:village_plains"
				}
			}
		}
	""".trimIndent()

	predicate("movement_sub") {
		entityProperties {
			movement {
				x(1)
				y(0.0, 1.0)
				z(-1..1)
				speed(2)
				horizontalSpeed(3)
				verticalSpeed(4)
				fallDistance(5..10)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:movement": {
					"x": 1.0,
					"y": {
						"min": 0.0,
						"max": 1.0
					},
					"z": {
						"min": -1.0,
						"max": 1.0
					},
					"speed": 2.0,
					"horizontal_speed": 3.0,
					"vertical_speed": 4.0,
					"fall_distance": {
						"min": 5.0,
						"max": 10.0
					}
				}
			}
		}
	""".trimIndent()

	predicate("movement_affected_by_sub") {
		entityProperties {
			movementAffectedBy {
				block(Blocks.HONEY_BLOCK)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:movement_affected_by": {
					"block": {
						"blocks": "minecraft:honey_block"
					}
				}
			}
		}
	""".trimIndent()

	predicate("nbt_sub") {
		entityProperties {
			nbt {
				this["foo"] = "bar"
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:nbt": {
					"foo": "bar"
				}
			}
		}
	""".trimIndent()

	predicate("passenger_sub") {
		entityProperties {
			passenger {
				entityType(EntityTypes.PIG)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:passenger": {
					"minecraft:entity_type": "minecraft:pig"
				}
			}
		}
	""".trimIndent()

	predicate("periodic_tick_sub") {
		entityProperties {
			periodicTick(20)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:periodic_tick": 20
			}
		}
	""".trimIndent()

	predicate("predicates_sub") {
		entityProperties {
			predicates {
				villagerVariant(VillagerTypes.TAIGA)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:predicates": {
					"minecraft:villager/variant": "minecraft:taiga"
				}
			}
		}
	""".trimIndent()

	predicate("slots_sub") {
		entityProperties {
			slots(WEAPON.MAINHAND to itemStackPredicate(Items.STICK))
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:slots": {
					"weapon.mainhand": {
						"items": "minecraft:stick"
					}
				}
			}
		}
	""".trimIndent()

	predicate("stepping_on_sub") {
		entityProperties {
			steppingOn {
				block(Blocks.DIRT)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:stepping_on": {
					"block": {
						"blocks": "minecraft:dirt"
					}
				}
			}
		}
	""".trimIndent()

	predicate("targeted_entity_sub") {
		entityProperties {
			targetedEntity {
				entityType(EntityTypes.PLAYER)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:targeted_entity": {
					"minecraft:entity_type": "minecraft:player"
				}
			}
		}
	""".trimIndent()

	predicate("team_sub") {
		entityProperties {
			team("red")
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:team": "red"
			}
		}
	""".trimIndent()

	predicate("vehicle_sub") {
		entityProperties {
			vehicle {
				entityType(EntityTypes.OAK_BOAT)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"entity": "this",
			"predicate": {
				"minecraft:vehicle": {
					"minecraft:entity_type": "minecraft:oak_boat"
				}
			}
		}
	""".trimIndent()
}

class PredicateEntitySubPredicateTests : FunSpec({
	test("predicate entity sub predicates") {
		dataPack("predicateEntitySubPredicate") {
			pretty()
			predicateEntitySubPredicateTests()
		}
	}
})
