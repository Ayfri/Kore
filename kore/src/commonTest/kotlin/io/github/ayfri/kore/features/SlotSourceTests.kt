package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.ARMOR
import io.github.ayfri.kore.arguments.HOTBAR
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.slotsources.*
import io.github.ayfri.kore.generated.arguments.tagged.SlotSourceTagArgument
import io.github.ayfri.kore.generated.arguments.types.SlotSourceArgument
import io.github.ayfri.kore.serializers.InlinableListSerializer
import io.github.ayfri.kore.utils.pretty
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf

private fun DataPack.slotSourceRoundTrip() {
	val json = slotSources.last().generateJson(this)
	val serializer = InlinableListSerializer(SlotSource.serializer())
	val decoded = jsonEncoder.decodeFromString(serializer, json)

	jsonEncoder.encodeToString(serializer, decoded) assertsIs json
}

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
			"slot_source_tests:hands",
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
	slotSourceRoundTrip()

	slotSource("only_reference") {
		reference(hands)
	}

	slotSources.last() assertsIs "\"slot_source_tests:hands\""
	slotSourceRoundTrip()

	slotSource("only_tag") {
		reference(SlotSourceTagArgument("all_hands", "slot_source_tests"))
	}

	slotSources.last() assertsIs "\"#slot_source_tests:all_hands\""

	slotSource("references") {
		reference(hands)
		reference(SlotSourceTagArgument("all_hands", "slot_source_tests"))
	}

	slotSources.last() assertsIs """
		[
			"slot_source_tests:hands",
			"#slot_source_tests:all_hands"
		]
	""".trimIndent()
	slotSourceRoundTrip()

	slotSource("group_tag") {
		group { reference(SlotSourceTagArgument("all_hands", "slot_source_tests")) }
	}

	slotSources.last() assertsIs """
		{
			"type": "minecraft:group",
			"terms": "#slot_source_tests:all_hands"
		}
	""".trimIndent()
	slotSourceRoundTrip()

	slotSource("nested") {
		contents(InventoryComponentType.CONTAINER) {
			slotSource { reference(hands) }
		}
		limitSlots(1) {
			slotSource {
				reference(hands)
				slotRange(SlotSourceOrigin.THIS, HOTBAR)
			}
		}
	}

	slotSources.last() assertsIs """
		[
			{
				"type": "minecraft:contents",
				"component": "minecraft:container",
				"slot_source": "slot_source_tests:hands"
			},
			{
				"type": "minecraft:limit_slots",
				"limit": 1,
				"slot_source": [
					"slot_source_tests:hands",
					{
						"type": "minecraft:slot_range",
						"source": "this",
						"slots": "hotbar.*"
					}
				]
			}
		]
	""".trimIndent()
	slotSourceRoundTrip()

	val id = jsonEncoder.decodeFromString(SlotSource.serializer(), "\"my_pack:hands\"")
		.shouldBeInstanceOf<SlotSourceReference>().id
	id.shouldBeInstanceOf<SlotSourceArgument>().asString() assertsIs "my_pack:hands"

	val tag = jsonEncoder.decodeFromString(SlotSource.serializer(), "\"#my_pack:t\"")
		.shouldBeInstanceOf<SlotSourceReference>().id
	tag.shouldBeInstanceOf<SlotSourceTagArgument>().asString() assertsIs "#my_pack:t"

	shouldThrowAny { jsonEncoder.decodeFromString(SlotSource.serializer(), "5.0") }
	shouldThrowAny { jsonEncoder.decodeFromString(SlotSource.serializer(), """{"type":"minecraft:reference","name":"my_pack:hands"}""") }
}

class SlotSourceTests : FunSpec({
	test("slot source") {
		dataPack("slot_source_tests") {
			pretty()
			slotSourceTests()
		}
	}
})
