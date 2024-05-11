package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.components.types.customName
import io.github.ayfri.kore.arguments.components.types.damage
import io.github.ayfri.kore.arguments.enums.MapDecoration
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.commands.items
import io.github.ayfri.kore.features.itemmodifiers.functions.*
import io.github.ayfri.kore.features.itemmodifiers.itemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.Mode
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.conditions.weatherCheck
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.generated.Tags

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
				"destination": "worldgen/structure/mineshaft",
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
			exclude(ComponentTypes.FOOD, ComponentTypes.FIREWORKS)
			include(ComponentTypes.BLOCK_ENTITY_DATA)
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

	itemModifier("set_attributes") {
		setAttribute(Attributes.GENERIC_SCALE) {
			amount = constant(0.5f)
			operation = AttributeModifierOperation.ADD_VALUE
			slot = listOf(EquipmentSlot.MAINHAND)
		}
	}

	itemModifiers.last() assertsIs """
		{
			"function": "minecraft:set_attributes",
			"modifiers": [
				{
					"attribute": "minecraft:generic.scale",
					"amount": 0.5,
					"operation": "add_value",
					"slot": "mainhand"
				}
			]
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
					"text": "test",
					"filtered": "test2"
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
