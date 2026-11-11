package io.github.ayfri.kore.website.playground.snippets.villagertrades

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.tradesets.amount
import io.github.ayfri.kore.features.tradesets.tradeSet
import io.github.ayfri.kore.features.tradesets.trades
import io.github.ayfri.kore.features.villagertrades.gives
import io.github.ayfri.kore.features.villagertrades.maxUses
import io.github.ayfri.kore.features.villagertrades.villagerTrade
import io.github.ayfri.kore.features.villagertrades.wants
import io.github.ayfri.kore.features.villagertrades.xp
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("farmer_trades") {
	pack { description = textComponent("Custom farmer trades, grouped in a trade set") }

	val hayTrade = villagerTrade("hay_for_emerald") {
		wants(Items.HAY_BLOCK)
		gives(Items.EMERALD)
		maxUses(16)
		xp(2)
	}

	val breadTrade = villagerTrade("emerald_for_bread") {
		wants(Items.EMERALD)
		gives(Items.BREAD, count = 6)
		maxUses(16)
		xp(1)
	}

	tradeSet("custom_farmer_novice") {
		trades(hayTrade, breadTrade)
		amount(1)
		allowDuplicates = false
	}
}
