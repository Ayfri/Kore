package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.components.item.damage
import io.github.ayfri.kore.arguments.components.item.enchantment
import io.github.ayfri.kore.arguments.components.item.enchantments
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.recipeGive
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.features.recipes.recipes
import io.github.ayfri.kore.features.recipes.recipesBuilder
import io.github.ayfri.kore.features.recipes.types.*
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.TrimPatterns

fun DataPack.recipeTest() {
	recipes {
		blasting("test_blasting") {
			ingredient {
				tag = Tags.Item.STONE_CRAFTING_MATERIALS
			}

			result = itemStack(Items.DIAMOND) {
				damage(4)
			}
			experience = 10.0
			cookingTime = 2000
		}

		craftingShaped("diamond_drows") {
			pattern(
				" S ",
				" S ",
				" D "
			)

			key("D", Items.DIAMOND)
			key("S", Items.STICK)

			result(Items.DIAMOND_SWORD)
		}
	}

	val bowsRecipe = recipesBuilder.craftingShaped("sowb") {
		pattern(
			" ~|",
			"~ |",
			" ~|"
		)

		keys {
			"~" to Items.STRING
			"|" to Items.STICK
		}

		result(Items.BOW)
	}

	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_shaped",
			"pattern": [
				" ~|",
				"~ |",
				" ~|"
			],
			"key": {
				"~": {
					"item": "minecraft:string"
				},
				"|": {
					"item": "minecraft:stick"
				}
			},
			"result": {
				"id": "minecraft:bow"
			}
		}
	""".trimIndent()

	load {
		recipeGive(allPlayers(), bowsRecipe)
	}

	recipes {
		craftingShaped("sharpness_1") {
			pattern(
				"E E",
				" B ",
				"E E",
			)

			keys {
				"E" to Items.EMERALD
				"B" to Items.BOOK
			}

			result(Items.ENCHANTED_BOOK) {
				enchantments {
					enchantment(Enchantments.SHARPNESS, 1)
				}
			}
		}
	}

	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_shaped",
			"pattern": [
				"E E",
				" B ",
				"E E"
			],
			"key": {
				"E": {
					"item": "minecraft:emerald"
				},
				"B": {
					"item": "minecraft:book"
				}
			},
			"result": {
				"id": "minecraft:enchanted_book",
				"components": {
					"enchantments": {
						"minecraft:sharpness": 1
					}
				}
			}
		}
	""".trimIndent()

	allRecipeTypesTests()
}

private fun DataPack.allRecipeTypesTests() {
	recipesBuilder.craftingTransmute("test_crafting_transmute") {
		input {
			tag = Tags.Item.STONE_CRAFTING_MATERIALS
		}

		material {
			tag = Tags.Item.REPAIRS_DIAMOND_ARMOR
		}

		result(Items.DIAMOND) {
			damage(4)
			!enchantments {}
		}
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_transmute",
			"input": {
				"tag": "minecraft:stone_crafting_materials"
			},
			"material": {
				"tag": "minecraft:repairs_diamond_armor"
			},
			"result": {
				"id": "minecraft:diamond",
				"components": {
					"damage": 4,
					"!enchantments": {}
				}
			}
		}
	""".trimIndent()

	recipesBuilder.smithingTransform("test_smithing_transform") {
		template = Items.DIAMOND_BLOCK
		base = Items.DIAMOND_SWORD
		addition = Items.NETHERITE_INGOT
		result = Items.NETHERITE_SWORD
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:smithing_transform",
			"template": "minecraft:diamond_block",
			"base": "minecraft:diamond_sword",
			"addition": "minecraft:netherite_ingot",
			"result": "minecraft:netherite_sword"
		}
	""".trimIndent()

	recipesBuilder.smithingTrim("test_smithing_trim") {
		template = Items.DIAMOND_BLOCK
		base = Items.DIAMOND_SWORD
		addition = Items.NETHERITE_INGOT
		pattern = TrimPatterns.SILENCE
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:smithing_trim",
			"template": "minecraft:diamond_block",
			"base": "minecraft:diamond_sword",
			"addition": "minecraft:netherite_ingot",
			"pattern": "minecraft:silence"
		}
	""".trimIndent()
}
