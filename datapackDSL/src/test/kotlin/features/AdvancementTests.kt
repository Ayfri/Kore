package features

import DataPack
import features.advancements.*
import features.advancements.triggers.ConsumeItem
import features.advancements.types.itemStack
import features.predicates.conditions.anyOf
import features.predicates.conditions.randomChance
import features.predicates.conditions.timeCheck
import generated.Items
import generated.LootTables
import generated.Recipes

fun DataPack.advancementTests() {
	advancement("test") {
		display(Items.DIAMOND_SWORD, "Hello", "World") {
			frameType = AdvancementFrameType.CHALLENGE
		}

		parent = generated.Advancements.Story.ROOT

		criteria(
			name = "test",
			triggerCondition = ConsumeItem(itemStack(Items.ENCHANTED_GOLDEN_APPLE))
		) {
			randomChance(chance = 0.5f)
			timeCheck(10f..20f)

			anyOf {
				timeCheck(10f..20f)
			}
		}

		requirements("test")
		rewards {
			experience = 10
			function = "test:gradient"
			loot = listOf(LootTables.Chests.IGLOO_CHEST)
			recipes = listOf(Recipes.POLISHED_BLACKSTONE_BRICK_STAIRS_FROM_POLISHED_BLACKSTONE_BRICKS_STONECUTTING)
		}
	}
}
