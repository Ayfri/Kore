import features.advancements.*
import features.advancements.triggers.ConsumeItem
import features.advancements.types.itemStack
import features.predicates.RandomChance
import features.predicates.TimeCheck
import features.predicates.providers.intRange
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
			triggerCondition = ConsumeItem(itemStack(Items.ENCHANTED_GOLDEN_APPLE)),
			RandomChance(chance = 0.5f),
			TimeCheck(value = intRange(10f..20f)),
		)

		requirements("test")
		rewards {
			experience = 10
			function = "test:gradient"
			loot = listOf(LootTables.Chests.IGLOO_CHEST)
			recipes = listOf(Recipes.POLISHED_BLACKSTONE_BRICK_STAIRS_FROM_POLISHED_BLACKSTONE_BRICKS_STONECUTTING)
		}
	}
}
