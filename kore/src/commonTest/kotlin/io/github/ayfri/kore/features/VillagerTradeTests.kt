package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.components.item.damage
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.itemmodifiers.functions.explosionDecay
import io.github.ayfri.kore.features.itemmodifiers.functions.limitCount
import io.github.ayfri.kore.features.itemmodifiers.functions.reference
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.reference
import io.github.ayfri.kore.features.predicates.providers.uniform
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
				"count": 1,
				"id": "minecraft:paper"
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
				"count": 10,
				"id": "minecraft:wheat"
			}
		}
	""".trimIndent()

	villagerTrade("trade_with_count_providers") {
		wants(Items.WHEAT, uniform(16, 24))
		additionalWants(Items.PAPER, reference(ContextIntProviderArgument("paper", "p")))
		gives(Items.EMERALD)
	}

	villagerTrades.last() assertsIs """
		{
			"additional_wants": {
				"count": "p:paper",
				"id": "minecraft:paper"
			},
			"gives": {
				"id": "minecraft:emerald"
			},
			"wants": {
				"count": {
					"type": "minecraft:uniform",
					"max": 24,
					"min": 16
				},
				"id": "minecraft:wheat"
			}
		}
	""".trimIndent()
	roundTrip(villagerTrades.last())

	villagerTrade("trade_with_component_cost") {
		wants(Items.DIAMOND_SWORD) {
			damage(0)
		}
		additionalWants(Items.PAPER)
		gives(Items.EMERALD)
	}

	villagerTrades.last() assertsIs """
		{
			"additional_wants": {
				"id": "minecraft:paper"
			},
			"gives": {
				"id": "minecraft:emerald"
			},
			"wants": {
				"components": {
					"damage": 0
				},
				"id": "minecraft:diamond_sword"
			}
		}
	""".trimIndent()
	roundTrip(villagerTrades.last())

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
