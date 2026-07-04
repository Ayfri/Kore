package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.components.item.damage
import io.github.ayfri.kore.arguments.components.item.enchantment
import io.github.ayfri.kore.arguments.components.item.enchantments
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.recipeGive
import io.github.ayfri.kore.features.recipes.recipes
import io.github.ayfri.kore.features.recipes.recipesBuilder
import io.github.ayfri.kore.features.recipes.types.*
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.TrimPatterns
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun DataPack.recipeTest() {
	recipes {
		blasting("test_blasting") {
			ingredient(Tags.Item.STONE_CRAFTING_MATERIALS)

			result(Items.DIAMOND) {
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
				"~": "minecraft:string",
				"|": "minecraft:stick"
			},
			"result": "minecraft:bow"
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
				"E": "minecraft:emerald",
				"B": "minecraft:book"
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
		input(Tags.Item.STONE_CRAFTING_MATERIALS)

		material(Tags.Item.REPAIRS_DIAMOND_ARMOR)

		result(Items.DIAMOND) {
			damage(4)
			!enchantments {}
		}
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_transmute",
			"input": "#minecraft:stone_crafting_materials",
			"material": "#minecraft:repairs_diamond_armor",
			"result": {
				"id": "minecraft:diamond",
				"components": {
					"damage": 4,
					"!enchantments": {}
				}
			}
		}
	""".trimIndent()

	recipesBuilder.craftingDye("test_crafting_dye") {
		dye(Items.RED_DYE)
		target(Tags.Item.WOOL)
		result(Items.RED_WOOL)
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_dye",
			"dye": "minecraft:red_dye",
			"result": "minecraft:red_wool",
			"target": "#minecraft:wool"
		}
	""".trimIndent()

	recipesBuilder.craftingImbue("test_crafting_imbue") {
		material(Tags.Item.ARROWS)
		source(Items.LINGERING_POTION)
		result(Items.TIPPED_ARROW)
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_imbue",
			"material": "#minecraft:arrows",
			"result": "minecraft:tipped_arrow",
			"source": "minecraft:lingering_potion"
		}
	""".trimIndent()

	recipesBuilder.craftingDecoratedPot("test_crafting_decorated_pot") {
		back(Tags.Item.DECORATED_POT_INGREDIENTS)
		front(Tags.Item.DECORATED_POT_INGREDIENTS)
		left(Tags.Item.DECORATED_POT_INGREDIENTS)
		right(Tags.Item.DECORATED_POT_INGREDIENTS)
		result(Items.DECORATED_POT)
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_decorated_pot",
			"back": "#minecraft:decorated_pot_ingredients",
			"front": "#minecraft:decorated_pot_ingredients",
			"left": "#minecraft:decorated_pot_ingredients",
			"result": "minecraft:decorated_pot",
			"right": "#minecraft:decorated_pot_ingredients"
		}
	""".trimIndent()

	recipesBuilder.craftingSpecialBannerDuplicate("test_crafting_special_bannerduplicate") {
		banner(Tags.Item.BANNERS)
		result(Items.WHITE_BANNER)
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_special_bannerduplicate",
			"banner": "#minecraft:banners",
			"result": "minecraft:white_banner"
		}
	""".trimIndent()

	recipesBuilder.craftingSpecialBookCloning("test_crafting_special_bookcloning") {
		source(Items.WRITTEN_BOOK)
		material(Items.WRITABLE_BOOK)
		result(Items.WRITTEN_BOOK)
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_special_bookcloning",
			"material": "minecraft:writable_book",
			"result": "minecraft:written_book",
			"source": "minecraft:written_book"
		}
	""".trimIndent()

	recipesBuilder.craftingSpecialFireworkRocket("test_crafting_special_firework_rocket") {
		fuel(Items.GUNPOWDER)
		shell(Items.PAPER)
		star(Items.FIREWORK_STAR)
		result(Items.FIREWORK_ROCKET)
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_special_firework_rocket",
			"fuel": "minecraft:gunpowder",
			"result": "minecraft:firework_rocket",
			"shell": "minecraft:paper",
			"star": "minecraft:firework_star"
		}
	""".trimIndent()

	recipesBuilder.craftingSpecialFireworkStar("test_crafting_special_firework_star") {
		dye(Items.RED_DYE)
		fuel(Items.GUNPOWDER)
		shape("burst", Items.FIRE_CHARGE)
		trail(Items.DIAMOND)
		twinkle(Items.GLOWSTONE_DUST)
		result(Items.FIREWORK_STAR)
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_special_firework_star",
			"dye": "minecraft:red_dye",
			"fuel": "minecraft:gunpowder",
			"result": "minecraft:firework_star",
			"shapes": {
				"burst": "minecraft:fire_charge"
			},
			"trail": "minecraft:diamond",
			"twinkle": "minecraft:glowstone_dust"
		}
	""".trimIndent()

	recipesBuilder.craftingSpecialFireworkStarFade("test_crafting_special_firework_star_fade") {
		dye(Items.BLUE_DYE)
		target(Items.FIREWORK_STAR)
		result(Items.FIREWORK_STAR)
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_special_firework_star_fade",
			"dye": "minecraft:blue_dye",
			"result": "minecraft:firework_star",
			"target": "minecraft:firework_star"
		}
	""".trimIndent()

	recipesBuilder.craftingSpecialMapExtending("test_crafting_special_mapextending") {
		map(Items.FILLED_MAP)
		material(Items.PAPER)
		result(Items.FILLED_MAP)
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_special_mapextending",
			"map": "minecraft:filled_map",
			"material": "minecraft:paper",
			"result": "minecraft:filled_map"
		}
	""".trimIndent()

	recipesBuilder.craftingSpecialShieldDecoration("test_crafting_special_shielddecoration") {
		banner(Tags.Item.BANNERS)
		target(Items.SHIELD)
		result(Items.SHIELD)
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:crafting_special_shielddecoration",
			"banner": "#minecraft:banners",
			"result": "minecraft:shield",
			"target": "minecraft:shield"
		}
	""".trimIndent()

	recipesBuilder.smithingTransform("test_smithing_transform") {
		template(Items.DIAMOND_BLOCK)
		base(Items.DIAMOND_SWORD)
		addition(Items.NETHERITE_INGOT, Items.NETHERRACK)
		result(Items.NETHERITE_SWORD)
	}
	recipes.last() assertsIs """
		{
			"type": "minecraft:smithing_transform",
			"template": "minecraft:diamond_block",
			"base": "minecraft:diamond_sword",
			"addition": [
				"minecraft:netherite_ingot",
				"minecraft:netherrack"
			],
			"result": "minecraft:netherite_sword"
		}
	""".trimIndent()

	recipesBuilder.smithingTrim("test_smithing_trim") {
		template(Items.DIAMOND_BLOCK)
		base(Items.DIAMOND_SWORD)
		addition(Items.NETHERITE_INGOT)
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

class RecipeTests : FunSpec({
	test("recipe") {
		testDataPack("recipe") {
			pretty()
			recipeTest()
		}.apply {
			assertGeneratorsGenerated()
			generate()
		}
	}
})
