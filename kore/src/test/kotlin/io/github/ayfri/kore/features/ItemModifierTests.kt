package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.components.item.*
import io.github.ayfri.kore.arguments.enums.MapDecoration
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.items
import io.github.ayfri.kore.features.itemmodifiers.functions.*
import io.github.ayfri.kore.features.itemmodifiers.itemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.Mode
import io.github.ayfri.kore.features.loottables.entries.item
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.conditions.weatherCheck
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.generated.Enchantments

fun DataPack.itemModifierTests() {
	val modifier = itemModifier("test_modifier") {
		explorationMap {
			decoration = MapDecoration.BANNER_BLACK
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
				"decoration": "banner_black",
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

	itemModifier("copy_name") {
		copyName(Source.LAST_DAMAGE_PLAYER)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:copy_name",
			"source": "last_damage_player"
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

	itemModifier("filtered") {
		filtered {
			itemFilter(Items.APPLE)

			modifiers {
				setName("Test")
			}
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:filtered",
			"item_filter": {
				"items": "minecraft:apple"
			},
			"modifier": {
				"function": "minecraft:set_name",
				"name": "Test"
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

	itemModifier("set_attributes") {
		setAttributes {
			attribute(Attributes.SCALE, 0.5f, slot = listOf(EquipmentSlot.MAINHAND))
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
					"operation": "add_value",
					"slot": "mainhand"
				}
			],
			"replace": false
		}
	""".trimIndent()

	itemModifier("set_components_modifier") {
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
				"custom_name": "{type:\"text\",color:\"black\",text:\"Test\"}",
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

	itemModifier("set_item") {
		setItem(Items.APPLE)
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_item",
			"item": "minecraft:apple"
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

	itemModifier("toggle_tooltips") {
		toggleTooltips {
			toggle(true, ToggleableComponents.TRIM, ToggleableComponents.CAN_PLACE_ON)
			toggles(ToggleableComponents.DYED_COLOR to false)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:toggle_tooltips",
			"toggles": {
				"trim": true,
				"can_place_on": true,
				"dyed_color": false
			}
		}
	""".trimIndent()
}
