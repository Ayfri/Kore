package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.components.item.*
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.items
import io.github.ayfri.kore.features.itemmodifiers.formulas.BinomialWithBonusCount
import io.github.ayfri.kore.features.itemmodifiers.functions.*
import io.github.ayfri.kore.features.itemmodifiers.itemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.Mode
import io.github.ayfri.kore.features.loottables.entries.item
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.conditions.weatherCheck
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.enchantmentLevel
import io.github.ayfri.kore.features.predicates.providers.providersRange
import io.github.ayfri.kore.features.predicates.providers.scoreNumber
import io.github.ayfri.kore.features.predicates.types.EntityType
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.utils.set

fun DataPack.itemModifierTests() {
	itemModifier("apply_bonus") {
		applyBonus(Enchantments.SHARPNESS, BinomialWithBonusCount(extra = 1, probability = 0.5f))
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:apply_bonus",
			"enchantment": "minecraft:sharpness",
			"formula": "minecraft:binomial_with_bonus_count",
			"parameters": {
				"extra": 1,
				"probability": 0.5
			}
		}
	""".trimIndent()

	itemModifier("copy_components") {
		copyComponents {
			exclude(ItemComponentTypes.FOOD, ItemComponentTypes.FIREWORKS)
			include(ItemComponentTypes.BLOCK_ENTITY_DATA)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:copy_components",
			"source": "block_entity",
			"include": [
				"minecraft:block_entity_data"
			],
			"exclude": [
				"minecraft:food",
				"minecraft:fireworks"
			]
		}
	""".trimIndent()

	itemModifier("copy_custom_data") {
		copyCustomData("kore:test") {
			append("foo", "bar")
			merge("baz", "qux")
			replace("quux", "corge")
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:copy_custom_data",
			"source": {
				"type": "minecraft:storage",
				"source": "kore:test"
			},
			"ops": [
				{
					"op": "append",
					"source": "foo",
					"target": "bar"
				},
				{
					"op": "merge",
					"source": "baz",
					"target": "qux"
				},
				{
					"op": "replace",
					"source": "quux",
					"target": "corge"
				}
			]
		}
	""".trimIndent()

	itemModifier("copy_name") {
		copyName(Source.ATTACKING_PLAYER)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:copy_name",
			"source": "attacking_player"
		}
	""".trimIndent()

	itemModifier("copy_state") {
		copyState(Blocks.OAK_LOG, "axis", "waterlogged")
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:copy_state",
			"block": "minecraft:oak_log",
			"properties": [
				"axis",
				"waterlogged"
			]
		}
	""".trimIndent()

	itemModifier("discard") {
		discard()
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:discard"
		}
	""".trimIndent()

	itemModifier("enchanted_count_increase") {
		enchantedCountIncrease(Enchantments.SHARPNESS, 1f, limit = 5)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:enchanted_count_increase",
			"enchantment": "minecraft:sharpness",
			"count": 1.0,
			"limit": 5
		}
	""".trimIndent()

	itemModifier("enchant_randomly") {
		enchantRandomly(Enchantments.SHARPNESS, Enchantments.LOOTING, onlyCompatible = true)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:enchant_randomly",
			"options": [
				"minecraft:sharpness",
				"minecraft:looting"
			],
			"only_compatible": true
		}
	""".trimIndent()

	itemModifier("enchant_with_levels") {
		enchantWithLevels(Enchantments.SHARPNESS, levels = constant(5f))
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:enchant_with_levels",
			"options": "minecraft:sharpness",
			"levels": 5.0
		}
	""".trimIndent()

	itemModifier("exploration_map") {
		explorationMap {
			decoration = MapDecorationTypes.FRAME
			destination = Tags.Worldgen.Structure.MINESHAFT
			searchRadius = 20
			skipExistingChunks = true
			zoom = 1
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:exploration_map",
			"destination": "minecraft:mineshaft",
			"decoration": "minecraft:frame",
			"zoom": 1,
			"search_radius": 20,
			"skip_existing_chunks": true
		}
	""".trimIndent()

	itemModifier("explosion_decay") {
		explosionDecay()
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:explosion_decay"
		}
	""".trimIndent()

	itemModifier("fill_player_head") {
		fillPlayerHead(Source.THIS)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:fill_player_head",
			"entity": "this"
		}
	""".trimIndent()

	itemModifier("filtered") {
		filtered {
			itemFilter(Items.APPLE)

			onPass {
				setName("Test")
			}

			onFail {
				discard()
			}
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:filtered",
			"item_filter": {
				"items": "minecraft:apple"
			},
			"on_fail": {
				"function": "minecraft:discard"
			},
			"on_pass": {
				"function": "minecraft:set_name",
				"name": "Test"
			}
		}
	""".trimIndent()

	itemModifier("furnace_smelt") {
		furnaceSmelt()
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:furnace_smelt"
		}
	""".trimIndent()

	itemModifier("limit_count") {
		limitCount(
			providersRange(
				min = scoreNumber("my_score", EntityType.THIS, 4f),
				max = enchantmentLevel(5)
			)
		)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:limit_count",
			"limit": {
				"min": {
					"type": "minecraft:score",
					"target": {
						"type": "minecraft:context",
						"target": "this"
					},
					"score": "my_score",
					"scale": 4.0
				},
				"max": {
					"type": "minecraft:enchantment_level",
					"amount": 5
				}
			}
		}
	""".trimIndent()

	itemModifier("modify_contents") {
		modifyContents(ContentComponentTypes.BUNDLE_CONTENTS) {
			modifiers {
				setName("Test")
			}
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:modify_contents",
			"component": "bundle_contents",
			"modifier": {
				"function": "minecraft:set_name",
				"name": "Test"
			}
		}
	""".trimIndent()

	itemModifier("reference") {
		reference("kore:my_modifier")
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:reference",
			"name": "kore:my_modifier"
		}
	""".trimIndent()

	itemModifier("sequence") {
		sequence {
			setItem(Items.APPLE)
			setName("Test", color = Color.BLACK)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:sequence",
			"functions": [
				{
					"function": "minecraft:set_item",
					"item": "minecraft:apple"
				},
				{
					"function": "minecraft:set_name",
					"name": {
						"text": "Test",
						"color": "black",
						"type": "text"
					}
				}
			]
		}
	""".trimIndent()

	itemModifier("set_attributes") {
		setAttributes {
			attribute(Attributes.SCALE, "test", 0.5f, slot = listOf(EquipmentSlot.MAINHAND))
			replace = false
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_attributes",
			"modifiers": [
				{
					"attribute": "minecraft:scale",
					"amount": 0.5,
					"id": "test",
					"operation": "add_value",
					"slot": "mainhand"
				}
			],
			"replace": false
		}
	""".trimIndent()

	itemModifier("set_banner_pattern") {
		setBannerPattern(append = true) {
			bannerPattern(BannerPatterns.CREEPER, FormattingColor.BLACK)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_banner_pattern",
			"patterns": [
				{
					"pattern": "minecraft:creeper",
					"color": "black"
				}
			],
			"append": true
		}
	""".trimIndent()

	itemModifier("set_book_cover") {
		setBookCover("Test Title", author = "Test Author", generation = 2)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_book_cover",
			"title": "\"Test Title\"",
			"author": "Test Author",
			"generation": 2
		}
	""".trimIndent()

	itemModifier("set_components") {
		setComponents {
			customName("Test", color = Color.BLACK)
			!damage(5)
			!"test"
			setToRemove("test2")
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_components",
			"components": {
				"custom_name": {
					"text": "Test",
					"color": "black"
				},
				"!damage": {},
				"!test": {},
				"!test2": {}
			}
		}
	""".trimIndent()

	itemModifier("set_contents") {
		setContents(ContentComponentTypes.BUNDLE_CONTENTS) {
			entries {
				item(Items.APPLE) {
					weight = 1
				}
			}
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_contents",
			"component": "bundle_contents",
			"entries": [
				{
					"type": "minecraft:item",
					"name": "minecraft:apple",
					"weight": 1
				}
			]
		}
	""".trimIndent()

	itemModifier("set_count") {
		setCount(5f, add = true)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_count",
			"count": 5.0,
			"add": true
		}
	""".trimIndent()

	itemModifier("set_custom_data") {
		setCustomData {
			this["test"] = 1
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_custom_data",
			"tag": {
				"test": 1
			}
		}
	""".trimIndent()

	itemModifier("set_custom_model_data") {
		setCustomModelData(
			colors = listOf(Color.RED, Color.BLUE),
			flags = listOf(true, false),
			floats = listOf(1.0f, 2.0f),
			strings = listOf("test1", "test2")
		)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_custom_model_data",
			"colors": {
				"values": [
					16733525,
					5592575
				],
				"mode": "replace_all"
			},
			"flags": {
				"values": [
					true,
					false
				],
				"mode": "replace_all"
			},
			"floats": {
				"values": [
					1.0,
					2.0
				],
				"mode": "replace_all"
			},
			"strings": {
				"values": [
					"test1",
					"test2"
				],
				"mode": "replace_all"
			}
		}
	""".trimIndent()

	itemModifier("set_custom_model_data_with_mode") {
		setCustomModelData(
			colors = listOf(Color.RED),
			flags = listOf(true)
		) {
			colors?.mode(Mode.INSERT, 1)
			flags?.mode(Mode.APPEND)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_custom_model_data",
			"colors": {
				"values": [
					16733525
				],
				"mode": "insert",
				"offset": 1
			},
			"flags": {
				"values": [
					true
				],
				"mode": "append"
			}
		}
	""".trimIndent()

	itemModifier("set_damage") {
		setDamage(0.5f, add = true)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_damage",
			"damage": 0.5,
			"add": true
		}
	""".trimIndent()

	itemModifier("set_enchantments") {
		setEnchantments(add = true) {
			this[Enchantments.SHARPNESS] = constant(2f)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_enchantments",
			"enchantments": {
				"minecraft:sharpness": 2.0
			},
			"add": true
		}
	""".trimIndent()

	itemModifier("set_firework_explosion") {
		setFireworkExplosion(FireworkExplosionShape.STAR) {
			colors = listOf(Color.RED.toRGB())
			fadeColors = listOf(Color.BLUE.toRGB())
			hasFlicker = true
			hasTrail = true
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_firework_explosion",
			"shape": "star",
			"colors": [
				16733525
			],
			"fade_colors": [
				5592575
			],
			"has_trail": true,
			"has_flicker": true
		}
	""".trimIndent()

	itemModifier("set_fireworks") {
		setFireworks {
			flightDuration = 5
			explosions {
				explosion(FireworkExplosionShape.BURST) {
					colors(Color.RED)
					fadeColors(Color.BLUE)
					hasTrail = true
					hasFlicker = true
				}

				mode(Mode.REPLACE_ALL)
			}
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_fireworks",
			"flight_duration": 5,
			"explosions": {
				"values": [
					{
						"shape": "burst",
						"colors": [
							16733525
						],
						"fade_colors": [
							5592575
						],
						"has_trail": true,
						"has_flicker": true
					}
				],
				"mode": "replace_all"
			}
		}
	""".trimIndent()

	itemModifier("set_item") {
		setItem(Items.APPLE)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_item",
			"item": "minecraft:apple"
		}
	""".trimIndent()

	itemModifier("set_loot_table") {
		setLootTable(BlockEntityTypes.CHEST, LootTables.Chests.SIMPLE_DUNGEON, seed = 42)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_loot_table",
			"type": "minecraft:chest",
			"name": "minecraft:chests/simple_dungeon",
			"seed": 42
		}
	""".trimIndent()

	itemModifier("set_lore") {
		setLore {
			lore("Test", color = Color.BLACK)
			lore("Test2")
			mode(Mode.INSERT, 0)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_lore",
			"lore": [
				{
					"text": "Test",
					"color": "black",
					"type": "text"
				},
				"Test2"
			],
			"mode": "insert",
			"offset": 0
		}
	""".trimIndent()

	itemModifier("set_name") {
		setName("Test", color = Color.BLACK) {
			target = SetNameTarget.CUSTOM_NAME
			entity = Source.THIS
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_name",
			"entity": "this",
			"name": {
				"text": "Test",
				"color": "black",
				"type": "text"
			},
			"target": "custom_name"
		}
	""".trimIndent()

	itemModifier("set_ominous_bottle_amplifier") {
		setOminousBottleAmplifier(5)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_ominous_bottle_amplifier",
			"amplifier": 5
		}
	""".trimIndent()

	itemModifier("set_potion") {
		setPotion(Potions.HEALING)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_potion",
			"id": "minecraft:healing"
		}
	""".trimIndent()

	itemModifier("set_stew_effect") {
		setStewEffect {
			potionEffect(Potions.NIGHT_VISION, constant(200f))
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_stew_effect",
			"effects": [
				{
					"type": "minecraft:night_vision",
					"duration": 200.0
				}
			]
		}
	""".trimIndent()

	itemModifier("set_writable_book_pages") {
		setWritableBookPages {
			page("test", filtered = "filtered")
			mode(Mode.INSERT, 1)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_writable_book_pages",
			"pages": [
				{
					"raw": "test",
					"filtered": "filtered"
				}
			],
			"mode": "insert",
			"offset": 1
		}
	""".trimIndent()

	itemModifier("set_written_book_pages") {
		setWrittenBookPages {
			page("test", filtered = "test2")
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_written_book_pages",
			"pages": [
				{
					"raw": "\"test\"",
					"filtered": "\"test2\""
				}
			],
			"mode": "replace_all"
		}
	""".trimIndent()

	val modifier = itemModifier("test_modifier") {
		explorationMap {
			decoration = MapDecorationTypes.BANNER_BLACK
			destination = Tags.Worldgen.Structure.MINESHAFT
			skipExistingChunks = true

			conditions {
				randomChance(0.5f)
				weatherCheck(true)
			}
		}

		setInstrument(Tags.Instrument.GOAT_HORNS)
	}

	itemModifiers.last() assertsIs """
		[
			{
				"function": "minecraft:exploration_map",
				"conditions": [
					{
						"condition": "minecraft:random_chance",
						"chance": 0.5
					},
					{
						"condition": "minecraft:weather_check",
						"raining": true
					}
				],
				"destination": "minecraft:mineshaft",
				"decoration": "minecraft:banner_black",
				"skip_existing_chunks": true
			},
			{
				"function": "minecraft:set_instrument",
				"options": "#minecraft:goat_horns"
			}
		]
	""".trimIndent()

	load {
		items {
			modify(self(), WEAPON.MAINHAND, modifier)
		}
	}

	itemModifier("toggle_tooltips") {
		toggleTooltips {
			toggle(true, ItemComponentTypes.TRIM, ItemComponentTypes.CAN_PLACE_ON)
			toggles(ItemComponentTypes.DYED_COLOR to false)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:toggle_tooltips",
			"toggles": {
				"minecraft:trim": true,
				"minecraft:can_place_on": true,
				"minecraft:dyed_color": false
			}
		}
	""".trimIndent()
}
