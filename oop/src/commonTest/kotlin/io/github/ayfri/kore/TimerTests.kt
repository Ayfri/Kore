package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.timer.registerTimer
import io.github.ayfri.kore.timer.registerTimerWithBossBar
import io.kotest.core.spec.style.FunSpec

fun timerTests() = dataPack("timer_tests") {
	val gameTimer = registerTimer("game_timer", 200.ticks)
	val player = player("Ayfri")

	function("start_timer") {
		gameTimer.start(player)
		lines[0] assertsIs "scoreboard players set @e[limit=1,name=Ayfri,type=minecraft:player] game_timer 0"
		lines.size assertsIs 1
	}

	function("stop_timer") {
		gameTimer.stop(player)
		lines[0] assertsIs "scoreboard players set @e[limit=1,name=Ayfri,type=minecraft:player] game_timer -1"
		lines.size assertsIs 1
	}

	function("on_complete") {
		gameTimer.onComplete(player) {
			say("Timer complete!")
		}
		lines[0].startsWith("execute if score @e[limit=1,name=Ayfri,type=minecraft:player] game_timer matches 200 run function timer_tests:generated_scopes/timer_game_timer_complete_") assertsIs true
		lines.size assertsIs 1
	}

	generatedFunctions.any { it.name == OopConstants.timerInitFunctionName("game_timer") } assertsIs true
	generatedFunctions.single { it.name == OopConstants.timerTickFunctionName("game_timer") }.toString() assertsIs
		"scoreboard players add @e[scores={game_timer=0..}] game_timer 1"
}

class TimerTests : FunSpec({
	test("timer") {
		timerTests()
	}

	test("timer durations in seconds are counted in ticks") {
		dataPack("timer_seconds") {
			val timed = registerTimerWithBossBar("round", 5.seconds)

			function("on_complete") {
				timed.timer.onComplete(player("Ayfri")) { say("done") }
				lines[0].startsWith("execute if score @e[limit=1,name=Ayfri,type=minecraft:player] round matches 100 run ") assertsIs true
			}

			timed.bossBar.config.max assertsIs 100
			generatedFunctions.single { it.name == OopConstants.timerBarTickFunctionName("round") }.toString() assertsIs
				"execute as @e[limit=1,scores={round=0..}] store result bossbar timer_seconds:round_bar value run scoreboard players get @s round"
		}
	}
})
