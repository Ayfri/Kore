package features

import DataPack
import arguments.WEAPON
import arguments.self
import commands.items
import features.itemmodifiers.conditions
import features.itemmodifiers.functions.MapDecoration
import features.itemmodifiers.functions.explorationMap
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
		}

		conditions {
			randomChance(0.5f)
			weatherCheck(true)
		}
	}

	load {
		items {
			modify(self(), WEAPON.MAINHAND, modifier)
		}
	}
}
