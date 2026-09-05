package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.components.item.enchantment
import io.github.ayfri.kore.arguments.components.item.enchantments
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.entities.FakePlayer
import io.github.ayfri.kore.entities.fakePlayer
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.entities.setScore
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.items.toNbt
import io.github.ayfri.kore.scoreboard.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ScoreHolderTests : FunSpec({
	test("fake players are emitted as bare score holders instead of name selectors") {
		val datapack = dataPack("oop_tests") {}

		val scores = datapack.function("fake_player_scores") {
			val total = fakePlayer("total")
			val line = FakePlayer("§0")

			total.setScore("game", 5)
			line.setScore("sidebar", -1)
			ScoreboardEntity("game", total).add(3)
		}

		scores.toString() assertsIs """
			scoreboard players set #total game 5
			scoreboard players set §0 sidebar -1
			scoreboard players add #total game 3
		""".trimIndent()
	}

	test("fake players refuse to produce an entity selector") {
		shouldThrow<IllegalStateException> { fakePlayer("total").asSelector() }
	}

	test("score operations cover both score operands and int constants") {
		val datapack = dataPack("oop_tests") {}

		val operations = datapack.function("score_operations") {
			val wave = ScoreboardEntity("game", fakePlayer("wave"))
			val kills = ScoreboardEntity("game", player("Ayfri"))

			wave += kills
			wave *= 2
			wave /= 2
			wave %= 2
			wave maxWith kills
			wave swapWith kills
		}

		operations.toString() assertsIs """
			scoreboard players operation #wave game += @e[limit=1,name=Ayfri,type=minecraft:player] game
			scoreboard objectives add kore_constants dummy
			scoreboard players set #2 kore_constants 2
			scoreboard players operation #wave game *= #2 kore_constants
			scoreboard players operation #wave game /= #2 kore_constants
			scoreboard players operation #wave game %= #2 kore_constants
			scoreboard players operation #wave game > @e[limit=1,name=Ayfri,type=minecraft:player] game
			scoreboard players operation #wave game >< @e[limit=1,name=Ayfri,type=minecraft:player] game
		""".trimIndent()
	}

	test("item stacks expose the item nbt compound used by data commands") {
		val stack = itemStack(Items.DIAMOND_SWORD, 3) {
			enchantments {
				enchantment(Enchantments.SHARPNESS, 5)
			}
		}

		stack.toNbt().toString() shouldBe
			"""{id:"minecraft:diamond_sword",count:3,components:{enchantments:{"minecraft:sharpness":5}}}"""
	}
})
