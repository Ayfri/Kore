package commands

import arguments.ARMOR
import arguments.CONTAINER
import arguments.numbers.PosNumber
import arguments.self
import arguments.vec3
import functions.Function
import generated.Items
import generated.LootTables
import utils.assertsIs

fun Function.lootTests() {
	loot(self()) {
		fish(LootTables.Gameplay.CAT_MORNING_GIFT, vec3(), Items.FISHING_ROD)
	} assertsIs "loot give @s fish minecraft:gameplay/cat_morning_gift ~ ~ ~ minecraft:fishing_rod"

	loot(self()) {
		fish(LootTables.Gameplay.CAT_MORNING_GIFT, vec3(), Hand.MAIN_HAND)
	} assertsIs "loot give @s fish minecraft:gameplay/cat_morning_gift ~ ~ ~ mainhand"

	loot(self()) {
		fish(LootTables.Gameplay.CAT_MORNING_GIFT, vec3())
	} assertsIs "loot give @s fish minecraft:gameplay/cat_morning_gift ~ ~ ~"

	loot(self()) {
		kill(self())
	} assertsIs "loot give @s kill @s"

	loot(self()) {
		loot(LootTables.Gameplay.CAT_MORNING_GIFT)
	} assertsIs "loot give @s loot minecraft:gameplay/cat_morning_gift"

	loot(self()) {
		mine(vec3(), Items.FISHING_ROD)
	} assertsIs "loot give @s mine ~ ~ ~ minecraft:fishing_rod"

	loot(self()) {
		mine(vec3(), Hand.MAIN_HAND)
	} assertsIs "loot give @s mine ~ ~ ~ mainhand"

	loot(self()) {
		mine(vec3())
	} assertsIs "loot give @s mine ~ ~ ~"

	loot(self(), LootTables.Gameplay.CAT_MORNING_GIFT) assertsIs "loot give @s loot minecraft:gameplay/cat_morning_gift"

	loot {
		target {
			insert(vec3())
		}

		source {
			kill(self())
		}
	} assertsIs "loot insert ~ ~ ~ kill @s"

	loot {
		target {
			spawn(vec3(PosNumber.Type.WORLD))
		}

		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	} assertsIs "loot spawn 0 0 0 loot minecraft:gameplay/cat_morning_gift"

	loot {
		target {
			replaceBlock(vec3(), CONTAINER[0])
		}

		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	} assertsIs "loot replace ~ ~ ~ container.0 loot minecraft:gameplay/cat_morning_gift"

	loot {
		target {
			replaceBlock(vec3(), CONTAINER[0], 1)
		}

		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	} assertsIs "loot replace ~ ~ ~ container.0 1 loot minecraft:gameplay/cat_morning_gift"

	loot {
		target {
			replaceEntity(self(), ARMOR.HEAD)
		}

		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	} assertsIs "loot replace @s armor.head loot minecraft:gameplay/cat_morning_gift"

	loot {
		target {
			replaceEntity(self(), ARMOR.HEAD, 1)
		}

		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	} assertsIs "loot replace @s armor.head 1 loot minecraft:gameplay/cat_morning_gift"
}
