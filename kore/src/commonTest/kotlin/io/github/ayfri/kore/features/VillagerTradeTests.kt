package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.itemmodifiers.functions.limitCount
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.villagertrades.*
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.villagerTradeTests() {
	villagerTrade("test_trade") {
		wants(Items.EMERALD)
		gives(Items.DIAMOND, count = 1)
		maxUses = constant(10f)
		xp = constant(5f)
	}

	villagerTrades.last() assertsIs """
		{
			"gives": {
				"id": "minecraft:diamond",
				"count": 1
			},
			"max_uses": 10.0,
			"wants": {
				"id": "minecraft:emerald"
			},
			"xp": 5.0
		}
	""".trimIndent()

	villagerTrade("trade_with_additional") {
		wants(Items.WHEAT, count = 10)
		additionalWants(Items.PAPER, count = 1)
		gives(Items.EMERALD)
		givenItemModifiers {
			limitCount(2)
		}
		maxUses = constant(3f)
	}

	villagerTrades.last() assertsIs """
		{
			"additional_wants": {
				"id": "minecraft:paper",
				"count": 1
			},
			"given_item_modifiers": [
				{
					"function": "minecraft:limit_count",
					"limit": 2
				}
			],
			"gives": {
				"id": "minecraft:emerald"
			},
			"max_uses": 3.0,
			"wants": {
				"id": "minecraft:wheat",
				"count": 10
			}
		}
	""".trimIndent()
}

class VillagerTradeTests : FunSpec({
	test("villager trade") {
		dataPack("villagerTrade") {
			pretty()
			villagerTradeTests()
		}
	}
})
