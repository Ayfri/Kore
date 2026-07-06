package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.tradesets.tradeSet
import io.github.ayfri.kore.features.villagertrades.gives
import io.github.ayfri.kore.features.villagertrades.villagerTrade
import io.github.ayfri.kore.features.villagertrades.wants
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.VillagerTrades
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.tradeSetTests() {
	val trade = villagerTrade("emerald_for_wheat") {
		wants(Items.WHEAT, count = 20)
		gives(Items.EMERALD)
	}

	tradeSet(
		"farmer_trades",
		trades = listOf(trade, Tags.VillagerTrade.Armorer.LEVEL_1, VillagerTrades.Fletcher.`1`.EMERALD_ARROW),
		amount = constant(2f),
	) {
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
}

class TradeSetTests : FunSpec({
	test("trade set") {
		dataPack("tradeSet") {
			pretty()
			tradeSetTests()
		}
	}
})
