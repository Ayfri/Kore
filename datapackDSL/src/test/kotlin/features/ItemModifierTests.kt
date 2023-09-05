package features

import DataPack
import arguments.WEAPON
import arguments.types.literals.self
import assertions.assertsIs
import commands.items
import features.itemmodifiers.functions.MapDecoration
import features.itemmodifiers.functions.conditions
import features.itemmodifiers.functions.explorationMap
import features.itemmodifiers.functions.setInstrument
import features.itemmodifiers.itemModifier
import features.predicates.conditions.randomChance
import features.predicates.conditions.weatherCheck
import functions.load
import generated.Tags

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
}
