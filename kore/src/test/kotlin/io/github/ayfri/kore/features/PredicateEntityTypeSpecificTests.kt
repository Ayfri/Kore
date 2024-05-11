package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.predicates.conditions.entityProperties
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.features.predicates.sub.entityspecific.*
import io.github.ayfri.kore.generated.*

fun DataPack.predicateEntityTypeSpecificTests() {
	predicate("axolotl_type_specific") {
		entityProperties {
			axolotlTypeSpecific(AxolotlVariants.LUCY)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:axolotl",
					"variant": "lucy"
				}
			}
		}
	""".trimIndent()

	predicate("boat_type_specific") {
		entityProperties {
			boatTypeSpecific(BoatVariants.OAK)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:boat",
					"variant": "oak"
				}
			}
		}
	""".trimIndent()

	predicate("cat_type_specific") {
		entityProperties {
			catTypeSpecific(CatVariants.WHITE)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:cat",
					"variant": "minecraft:white"
				}
			}
		}
	""".trimIndent()

	predicate("fishing_hook_type_specific") {
		entityProperties {
			fishingHookTypeSpecific(inOpenWater = true)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:fishing_hook",
					"in_open_water": true
				}
			}
		}
	""".trimIndent()

	predicate("fox_type_specific") {
		entityProperties {
			foxTypeSpecific(FoxVariants.RED)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:fox",
					"variant": "red"
				}
			}
		}
	""".trimIndent()

	predicate("frog_type_specific") {
		entityProperties {
			frogTypeSpecific(FrogVariants.COLD, FrogVariants.WARM)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:frog",
					"variant": [
						"minecraft:cold",
						"minecraft:warm"
					]
				}
			}
		}
	""".trimIndent()

	predicate("horse_type_specific") {
		entityProperties {
			horseTypeSpecific(HorseVariants.BLACK)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:horse",
					"variant": "black"
				}
			}
		}
	""".trimIndent()

	predicate("lightning_type_specific") {
		entityProperties {
			lightningTypeSpecific {
				blocksSetOnFire = rangeOrInt(1..5)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:lightning",
					"blocks_set_on_fire": {
						"min": 1,
						"max": 5
					}
				}
			}
		}
	""".trimIndent()

	predicate("llama_type_specific") {
		entityProperties {
			llamaTypeSpecific(LlamaVariants.BROWN)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:llama",
					"variant": "brown"
				}
			}
		}
	""".trimIndent()

	predicate("mooshroom_type_specific") {
		entityProperties {
			mooshroomTypeSpecific(MooshroomVariants.BROWN)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:mooshroom",
					"variant": "brown"
				}
			}
		}
	""".trimIndent()

	predicate("painting_type_specific") {
		entityProperties {
			paintingTypeSpecific(PaintingVariants.ALBAN, PaintingVariants.AZTEC)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:painting",
					"variant": [
						"minecraft:alban",
						"minecraft:aztec"
					]
				}
			}
		}
	""".trimIndent()

	predicate("parrot_type_specific") {
		entityProperties {
			parrotTypeSpecific(ParrotVariants.BLUE)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:parrot",
					"variant": "blue"
				}
			}
		}
	""".trimIndent()

	predicate("player_type_specific") {
		entityProperties {
			playerTypeSpecific {
				gamemode = Gamemode.CREATIVE
				recipes {
					this[Recipes.BOW] = true
				}
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:player",
					"gamemode": "creative",
					"recipes": {
						"minecraft:bow": true
					}
				}
			}
		}
	""".trimIndent()

	predicate("rabbit_type_specific") {
		entityProperties {
			rabbitTypeSpecific(RabbitVariants.BLACK)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:rabbit",
					"variant": "black"
				}
			}
		}
	""".trimIndent()

	predicate("raider_type_specific") {
		entityProperties {
			raiderTypeSpecific(hasRaid = true, isCaptain = false)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:raider",
					"has_raid": true,
					"is_captain": false
				}
			}
		}
	""".trimIndent()

	predicate("slime_type_specific") {
		entityProperties {
			slimeTypeSpecific(rangeOrInt(2))
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:slime",
					"size": 2
				}
			}
		}
	""".trimIndent()

	predicate("tropical_fish_type_specific") {
		entityProperties {
			tropicalFishTypeSpecific(TropicalFishVariants.BRINELY)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:tropical_fish",
					"variant": "brinely"
				}
			}
		}
	""".trimIndent()

	predicate("villager_type_specific") {
		entityProperties {
			villagerTypeSpecific(VillagerTypes.JUNGLE)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:villager",
					"variant": "minecraft:jungle"
				}
			}
		}
	""".trimIndent()

	predicate("wolf_type_specific") {
		entityProperties {
			wolfTypeSpecific {
				variants(WolfVariants.WOODS)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:wolf",
					"variant": "minecraft:woods"
				}
			}
		}
	""".trimIndent()
}
