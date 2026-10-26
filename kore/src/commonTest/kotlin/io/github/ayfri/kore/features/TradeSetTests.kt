package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.providers.binomial
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
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

fun DataPack.tradeSetTests() {
	val wheatTrade = villagerTrade("emerald_for_wheat") {
		wants(Items.WHEAT, count = 20)
		gives(Items.EMERALD)
	}

	tradeSet("empty")
	tradeSets.last() assertsIs """
		{
			"amount": 1,
			"trades": []
		}
	""".trimIndent()

	tradeSet("single_trade") {
		trades(wheatTrade)
	}
	tradeSets.last() assertsIs """
		{
			"amount": 1,
			"trades": "tradeSet:emerald_for_wheat"
		}
	""".trimIndent()

	tradeSet("farmer_trades") {
		trades(wheatTrade, Tags.VillagerTrade.Armorer.LEVEL_1, VillagerTrades.Fletcher.`1`.EMERALD_ARROW)
		amount(2)
		allowDuplicates = false
	}
	tradeSets.last() assertsIs """
		{
			"allow_duplicates": false,
			"amount": 2,
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
		amount(1, 3)
		randomSequence("trade_set/appended_trades", "tradeSet")
	}
	tradeSets.last() assertsIs """
		{
			"amount": {
				"type": "minecraft:uniform",
				"max": 3,
				"min": 1
			},
			"random_sequence": "tradeSet:trade_set/appended_trades",
			"trades": [
				"tradeSet:emerald_for_wheat",
				"minecraft:fletcher/1/emerald_arrow"
			]
		}
	""".trimIndent()

	tradeSet("binomial_amount") {
		trades(wheatTrade)
		amount(binomial(4, 0.5f))
	}
	tradeSets.last() assertsIs """
		{
			"amount": {
				"type": "minecraft:binomial",
				"n": 4,
				"p": 0.5
			},
			"trades": "tradeSet:emerald_for_wheat"
		}
	""".trimIndent()

	tradeSet("range_amount") {
		trades(wheatTrade)
		amount(1..3)
	}
	tradeSets.last() assertsIs """
		{
			"amount": {
				"type": "minecraft:uniform",
				"max": 3,
				"min": 1
			},
			"trades": "tradeSet:emerald_for_wheat"
		}
	""".trimIndent()

	tradeSet("equal_bounds_amount") {
		trades(wheatTrade)
		amount(2, 2)
	}
	tradeSets.last() assertsIs """
		{
			"amount": 2,
			"trades": "tradeSet:emerald_for_wheat"
		}
	""".trimIndent()

	shouldThrow<IllegalArgumentException> {
		tradeSet("inverted_bounds_amount") { amount(3, 1) }
	}.message assertsIs "Cannot draw from the empty range 3..1."

	val subFolder = tradeSet("farmer/level_1") {
		trades(Tags.VillagerTrade.Farmer.LEVEL_1)
		amount = uniform(2, 4)
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
