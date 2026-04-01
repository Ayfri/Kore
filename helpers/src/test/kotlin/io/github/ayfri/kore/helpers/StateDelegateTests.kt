package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.entities.entity
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.helpers.assertions.assertsIs
import io.github.ayfri.kore.helpers.state.*
import io.github.ayfri.kore.scoreboard.minusAssign
import io.github.ayfri.kore.scoreboard.plusAssign
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StateDelegateTests : FunSpec({
	test("scoreboard delegates mix direct set and relative scoreboard operations") {
		val datapack = dataPack("helpers_tests") {}

		val stateFunction = datapack.function("state_delegate_scoreboard") {
			val player = entity()
			var mana by player.scoreboard("mana_obj", default = 100)
			val manaScore = player.scoreboardEntity("mana_obj")

			mana shouldBe 100
			mana = 80
			manaScore += 10
			manaScore -= 3
		}

		stateFunction.toString() assertsIs """
			scoreboard objectives add mana_obj dummy
			scoreboard players set @e[limit=1] mana_obj 80
			scoreboard players add @e[limit=1] mana_obj 10
			scoreboard players remove @e[limit=1] mana_obj 3
		""".trimIndent()
	}

	test("state delegates mix scoreboard and storage-backed state in one function") {
		val datapack = dataPack("helpers_tests") {}

		val stateFunction = datapack.function("state_delegate_storage") {
			val player = entity()
			var combo by player.scoreboard("combo")
			val comboScore = player.scoreboardEntity("combo")
			var status by player.storage("helpers:state", "session.status", default = "idle")
			var hasShield by player.storage("helpers:state", "session.has_shield", default = false)

			combo shouldBe 0
			status shouldBe "idle"
			hasShield shouldBe false

			combo = 1
			comboScore += 4
			status = "charged"
			hasShield = true
		}

		stateFunction.toString() assertsIs """
			scoreboard objectives add combo dummy
			scoreboard players set @e[limit=1] combo 1
			scoreboard players add @e[limit=1] combo 4
			data modify storage helpers:state session.status set value "charged"
			data modify storage helpers:state session.has_shield set value true
		""".trimIndent()
	}

	test("scoreboard delegates expose runIf, runWhile and repeatScore helpers with correct syntax") {
		val datapack = dataPack("helpers_tests") {}

		val stateFunction = datapack.function("state_delegate_control_flow") {
			val player = entity()
			val combo = player.scoreboard("combo")
			val threshold = player.scoreboard("threshold")
			val repeats = player.scoreboard("repeats")
			var comboValue by combo
			var thresholdValue by threshold
			var repeatCount by repeats

			comboValue = 45
			thresholdValue = 45

			runIf(combo equalTo 45, name = "combo_ready_if") {
				say("Combo ready")
			}

			runIf(combo equalTo threshold, name = "combo_equals_threshold") {
				say("Combo equals threshold")
			}

			runWhile(combo greaterThan 0, name = "combo_while") {
				say("Loop tick")
				combo.scoreboardEntity() -= 1
			}

			comboValue = 9
			repeatCount = 3
			repeatScore(combo, counter = repeats, name = "combo_repeat") {
				say("Repeat tick")
			}
		}

		stateFunction.toString() assertsIs """
			scoreboard objectives add combo dummy
			scoreboard objectives add threshold dummy
			scoreboard objectives add repeats dummy
			scoreboard players set @e[limit=1] combo 45
			scoreboard players set @e[limit=1] threshold 45
			execute if score @e[limit=1] combo matches 45 run function helpers_tests:generated_scopes/combo_ready_if
			execute if score @e[limit=1] combo = @e[limit=1] threshold run function helpers_tests:generated_scopes/combo_equals_threshold
			execute if score @e[limit=1] combo matches 1.. run function helpers_tests:generated_scopes/combo_while
			scoreboard players set @e[limit=1] combo 9
			scoreboard players set @e[limit=1] repeats 3
			execute if score @e[limit=1] repeats matches 1.. run function helpers_tests:generated_scopes/combo_repeat
		""".trimIndent()

		datapack.generatedFunctions.map { it.toString() } shouldBe listOf(
			"""
				say Combo ready
			""".trimIndent(),
			"""
				say Combo equals threshold
			""".trimIndent(),
			"""
				say Loop tick
				scoreboard players remove @e[limit=1] combo 1
				execute if score @e[limit=1] combo matches 1.. run function helpers_tests:generated_scopes/combo_while
			""".trimIndent(),
			"""
				say Repeat tick
				scoreboard players remove @e[limit=1] repeats 1
				execute if score @e[limit=1] repeats matches 1.. run function helpers_tests:generated_scopes/combo_repeat
			""".trimIndent(),
		)
	}
})