package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.ARMOR
import io.github.ayfri.kore.arguments.HOTBAR
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.slotsources.*
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.slotSourceTests() {
	val hands = slotSource("hands") {
		slotRange(WEAPON.all())
	}

	hands.asId() assertsIs "slot_source_tests:hands"
	slotSources.last() assertsIs """
		{
			"type": "minecraft:slot_range",
			"slots": "weapon.*"
		}
	""".trimIndent()

	slotSource("equipment") {
		reference(hands)
		slotRange(SlotSourceOrigin.CONTAINER, ARMOR)
		limitSlots(2) {
			slotSource { slotRange(SlotSourceOrigin.THIS, HOTBAR) }
		}
	}

	slotSources.last() assertsIs """
		[
			{
				"type": "minecraft:reference",
				"name": "slot_source_tests:hands"
			},
			{
				"type": "minecraft:slot_range",
				"source": "container",
				"slots": "armor.*"
			},
			{
				"type": "minecraft:limit_slots",
				"limit": 2,
				"slot_source": {
					"type": "minecraft:slot_range",
					"source": "this",
					"slots": "hotbar.*"
				}
			}
		]
	""".trimIndent()
}

class SlotSourceTests : FunSpec({
	test("slot source") {
		dataPack("slot_source_tests") {
			pretty()
			slotSourceTests()
		}
	}
})
