package io.github.ayfri.kore.minigames

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.colors.BossBarColor
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.bossbar.registerBossBar
import io.github.ayfri.kore.commands.BossBarStyle
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.entities.sendMessage
import io.github.ayfri.kore.entities.showTitle
import io.github.ayfri.kore.events.onKill
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.gamestate.registerGameStates
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.spawner.registerSpawner
import io.github.ayfri.kore.timer.registerTimer
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun kingOfTheHillTest() = testDataPack("koth") {
	val player = player("Fighter")
	val states = registerGameStates {
		state("lobby")
		state("active")
		state("ended")
	}
	val roundTimer = registerTimer("round_timer", 6000.ticks)
	val bar = registerBossBar("round_bar", name) {
		color = BossBarColor.YELLOW
		max = 300
		style = BossBarStyle.NOTCHED_20
	}
	val zombieSpawner = registerSpawner("zombie_wave", EntityTypes.ZOMBIE) {
		position = vec3(0, 64, 0)
	}

	function("start_game") {
		states.transitionTo("active")
		lines[0] assertsIs "scoreboard players set #game_state kore_state 1"

		roundTimer.start(player)
		lines[1] assertsIs "scoreboard players set @e[limit=1,name=Fighter,type=minecraft:player] round_timer 0"

		bar.setPlayers(player) assertsIs "bossbar set koth:round_bar players @e[limit=1,name=Fighter,type=minecraft:player]"
		bar.show() assertsIs "bossbar set koth:round_bar visible true"
		bar.setValue(300) assertsIs "bossbar set koth:round_bar value 300"

		zombieSpawner.spawnMultiple(3)
		lines[5] assertsIs "summon minecraft:zombie 0.0 64.0 0.0"
		lines[6] assertsIs "summon minecraft:zombie 0.0 64.0 0.0"
		lines[7] assertsIs "summon minecraft:zombie 0.0 64.0 0.0"

		lines.size assertsIs 8
	}

	function("on_kill_event") {
		player.onKill { say("Enemy eliminated!") }
	}

	function("end_game") {
		states.transitionTo("ended")
		lines[0] assertsIs "scoreboard players set #game_state kore_state 2"

		roundTimer.stop(player)
		lines[1] assertsIs "scoreboard players set @e[limit=1,name=Fighter,type=minecraft:player] round_timer -1"

		bar.hide() assertsIs "bossbar set koth:round_bar visible false"
		player.sendMessage("Game over!") assertsIs """tellraw @e[limit=1,name=Fighter,type=minecraft:player] "Game over!""""
		player.showTitle("Victory!")
		lines[4] assertsIs """title @e[limit=1,name=Fighter,type=minecraft:player] title "Victory!""""

		lines.size assertsIs 5
	}

	function("lobby_setup") {
		states.whenState("lobby") {
			say("Waiting for players...")
		}
		lines[0].startsWith("execute if score #game_state kore_state matches 0 run function koth:generated_scopes/state_lobby_handler_") assertsIs true
		lines.size assertsIs 1
	}

	function("round_complete") {
		roundTimer.onComplete(player) {
			say("Round over!")
		}
		lines[0].startsWith("execute if score @e[limit=1,name=Fighter,type=minecraft:player] round_timer matches 6000 run function koth:generated_scopes/timer_round_timer_complete_") assertsIs true
		lines.size assertsIs 1
	}

	generatedFunctions.any { it.name == OopConstants.timerInitFunctionName("round_timer") } assertsIs true
	generatedFunctions.any { it.name == OopConstants.timerTickFunctionName("round_timer") } assertsIs true
	generatedFunctions.any { it.name == OopConstants.spawnerSpawnFunctionName("zombie_wave") } assertsIs true
	generatedFunctions.any { it.name == "kore_state_init" } assertsIs true
}.apply {
	generate()
}

class KingOfTheHillTests : FunSpec({
	test("king of the hill") {
		kingOfTheHillTest()
	}
})
