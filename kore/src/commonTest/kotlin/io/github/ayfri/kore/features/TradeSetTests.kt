package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.providers.uniform
import io.github.ayfri.kore.features.tradesets.amount
import io.github.ayfri.kore.features.tradesets.randomSequence
import io.github.ayfri.kore.features.tradesets.trade
import io.github.ayfri.kore.features.tradesets.tradeSet
import io.github.ayfri.kore.features.tradesets.trades
import io.github.ayfri.kore.features.villagertrades.gives
import io.github.ayfri.kore.features.villagertrades.villagerTrade
import io.github.ayfri.kore.features.villagertrades.wants
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.VillagerTrades
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.tradeSetTests() {
	val wheatTrade = villagerTrade("emerald_for_wheat") {
		wants(Items.WHEAT, count = 20)
		gives(Items.EMERALD)
	}

	tradeSet("empty")
	tradeSets.last() assertsIs """
		{
			"amount": 1.0,
			"trades": []
		}
	""".trimIndent()

	tradeSet("single_trade") {
		trades(wheatTrade)
	}
	tradeSets.last() assertsIs """
		{
			"amount": 1.0,
			"trades": "tradeSet:emerald_for_wheat"
		}
	""".trimIndent()

	tradeSet("farmer_trades") {
		trades(wheatTrade, Tags.VillagerTrade.Armorer.LEVEL_1, VillagerTrades.Fletcher.`1`.EMERALD_ARROW)
		amount(2f)
		allowDuplicates = false
	}
	tradeSets.last() assertsIs """
		{
			"allow_duplicates": false,
			"amount": 2.0,
			"trades": [
				"tradeSet:emerald_for_wheat",
				"#minecraft:armorer/level_1",
				"minecraft:fletcher/1/emerald_arrow"
			]
		}
	""".trimIndent()

	tradeSet("appended_trades") {
		trades(listOf(wheatTrade))
		trade(VillagerTrades.Fletcher.`1`.EMERALD_ARROW)
		amount(1f, 3f)
		randomSequence("trade_set/appended_trades", "tradeSet")
	}
	tradeSets.last() assertsIs """
		{
			"amount": {
				"type": "minecraft:uniform",
				"min": 1.0,
				"max": 3.0
			},
			"random_sequence": "tradeSet:trade_set/appended_trades",
			"trades": [
				"tradeSet:emerald_for_wheat",
				"minecraft:fletcher/1/emerald_arrow"
			]
		}
	""".trimIndent()

	val subFolder = tradeSet("farmer/level_1") {
		trades(Tags.VillagerTrade.Farmer.LEVEL_1)
		amount = uniform(2f, 4f)
	}
	subFolder assertsIs "tradeSet:farmer/level_1"
	tradeSets.last().fileName assertsIs "farmer/level_1"
	tradeSets.last().resourceFolder assertsIs "trade_set"
}

class TradeSetTests : FunSpec({
	test("trade set") {
		dataPack("tradeSet") {
			pretty()
			tradeSetTests()
		}
	}
})
