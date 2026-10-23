package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.itemmodifiers.functions.explosionDecay
import io.github.ayfri.kore.features.itemmodifiers.functions.limitCount
import io.github.ayfri.kore.features.itemmodifiers.functions.reference
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.reference
import io.github.ayfri.kore.features.villagertrades.*
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.villagerTradeTests() {
	villagerTrade("test_trade") {
		wants(Items.EMERALD)
		gives(Items.DIAMOND, count = 1)
		maxUses = constant(10)
		xp = constant(5)
	}

	villagerTrades.last() assertsIs """
		{
			"gives": {
				"id": "minecraft:diamond",
				"count": 1
			},
			"max_uses": 10,
			"wants": {
				"id": "minecraft:emerald"
			},
			"xp": 5
		}
	""".trimIndent()

	villagerTrade("trade_with_references") {
		wants(Items.EMERALD)
		gives(Items.DIAMOND)
		maxUses = reference(ContextIntProviderArgument("uses", "p"))
		reputationDiscount = reference(ContextFloatProviderArgument("discount", "p"))
		xp = reference(ContextIntProviderArgument("xp", "p"))
	}

	villagerTrades.last() assertsIs """
		{
			"gives": {
				"id": "minecraft:diamond"
			},
			"max_uses": "p:uses",
			"reputation_discount": "p:discount",
			"wants": {
				"id": "minecraft:emerald"
			},
			"xp": "p:xp"
		}
	""".trimIndent()
	roundTrip(villagerTrades.last())

	villagerTrade("trade_with_additional") {
		wants(Items.WHEAT, count = 10)
		additionalWants(Items.PAPER, count = 1)
		gives(Items.EMERALD)
		givenItemModifier {
			limitCount(max = constant(2))
		}
		maxUses = constant(3)
	}

	villagerTrades.last() assertsIs """
		{
			"additional_wants": {
				"id": "minecraft:paper",
				"count": 1
			},
			"given_item_modifier": {
				"type": "minecraft:limit_count",
				"limit": {
					"max": 2
				}
			},
			"gives": {
				"id": "minecraft:emerald"
			},
			"max_uses": 3,
			"wants": {
				"id": "minecraft:wheat",
				"count": 10
			}
		}
	""".trimIndent()

	villagerTrade("trade_with_two_modifiers") {
		wants(Items.EMERALD)
		gives(Items.IRON_BOOTS)
		givenItemModifier {
			limitCount(max = constant(2))
			explosionDecay()
		}
	}

	villagerTrades.last() assertsIs """
		{
			"given_item_modifier": [
				{
					"type": "minecraft:limit_count",
					"limit": {
						"max": 2
					}
				},
				{
					"type": "minecraft:explosion_decay"
				}
			],
			"gives": {
				"id": "minecraft:iron_boots"
			},
			"wants": {
				"id": "minecraft:emerald"
			}
		}
	""".trimIndent()

	villagerTrade("trade_with_modifier_id") {
		wants(Items.EMERALD)
		gives(Items.IRON_BOOTS)
		givenItemModifier {
			reference(ItemModifierArgument("enchant_boots", "my_pack"))
		}
	}

	villagerTrades.last() assertsIs """
		{
			"given_item_modifier": "my_pack:enchant_boots",
			"gives": {
				"id": "minecraft:iron_boots"
			},
			"wants": {
				"id": "minecraft:emerald"
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
