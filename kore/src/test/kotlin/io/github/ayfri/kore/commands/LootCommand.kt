package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.PosNumber
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.LootTables

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
			replaceBlock(vec3(), io.github.ayfri.kore.arguments.CONTAINER[0])
		}

		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	} assertsIs "loot replace block ~ ~ ~ container.0 loot minecraft:gameplay/cat_morning_gift"

	loot {
		target {
			replaceBlock(vec3(), io.github.ayfri.kore.arguments.CONTAINER[0], 1)
		}

		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	} assertsIs "loot replace block ~ ~ ~ container.0 1 loot minecraft:gameplay/cat_morning_gift"

	loot {
		target {
			replaceEntity(self(), io.github.ayfri.kore.arguments.ARMOR.HEAD)
		}

		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	} assertsIs "loot replace entity @s armor.head loot minecraft:gameplay/cat_morning_gift"

	loot {
		target {
			replaceEntity(self(), io.github.ayfri.kore.arguments.ARMOR.HEAD, 1)
		}

		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	} assertsIs "loot replace entity @s armor.head 1 loot minecraft:gameplay/cat_morning_gift"
}
