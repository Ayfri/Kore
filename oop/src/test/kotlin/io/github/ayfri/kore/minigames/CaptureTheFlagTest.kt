package io.github.ayfri.kore.minigames

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.colors.BossBarColor
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.bossbar.registerBossBar
import io.github.ayfri.kore.commands.BossBarStyle
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.cooldown.registerCooldown
import io.github.ayfri.kore.entities.*
import io.github.ayfri.kore.events.onHurtEntity
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.gamestate.registerGameStates
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.spawner.registerSpawner
import io.github.ayfri.kore.timer.registerTimer
import io.github.ayfri.kore.utils.testDataPack

fun captureTheFlagTest() = testDataPack("ctf") {
    val attacker = player("Attacker")
    val defender = player("Defender")

    val states = registerGameStates {
        state("warmup")
        state("running")
        state("overtime")
        state("finished")
    }

    val gameTimer = registerTimer("game_clock", 12000.ticks)
    val sprintCooldown = registerCooldown("sprint_boost", 5.seconds)

    val bar = registerBossBar("game_bar", name) {
        color = BossBarColor.GREEN
        max = 600
        style = BossBarStyle.NOTCHED_20
    }

    val guardSpawner = registerSpawner("flag_guard", EntityTypes.IRON_GOLEM) {
        position = vec3(50, 64, 50)
    }

    function("setup_teams") {
        attacker.joinTeam("blue") assertsIs "team join blue @e[limit=1,name=Attacker,team=,type=minecraft:player]"
        defender.joinTeam("red") assertsIs "team join red @e[limit=1,name=Defender,team=,type=minecraft:player]"
        attacker.setGamemode(Gamemode.SURVIVAL) assertsIs "gamemode survival @e[limit=1,name=Attacker,team=blue,type=minecraft:player]"
        defender.setGamemode(Gamemode.SURVIVAL) assertsIs "gamemode survival @e[limit=1,name=Defender,team=red,type=minecraft:player]"
        lines.size assertsIs 4
    }

    function("start_round") {
        states.transitionTo("running")
        lines[0] assertsIs "scoreboard players set #game_state kore_state 1"

        gameTimer.start(attacker)
        lines[1] assertsIs "scoreboard players set @e[limit=1,name=Attacker,team=blue,type=minecraft:player] game_clock 0"

        bar.setPlayers(attacker) assertsIs "bossbar set ctf:game_bar players @e[limit=1,name=Attacker,team=blue,type=minecraft:player]"
        bar.show() assertsIs "bossbar set ctf:game_bar visible true"

        guardSpawner.spawn() assertsIs "summon minecraft:iron_golem 50 64 50"

        attacker.giveEffect(
            MobEffectArgument("speed"),
            duration = 100,
            amplifier = 1
        ) assertsIs "effect give @e[limit=1,name=Attacker,team=blue,type=minecraft:player] minecraft:speed 100 1"
        defender.giveEffect(
            MobEffectArgument("resistance"),
            duration = 200
        ) assertsIs "effect give @e[limit=1,name=Defender,team=red,type=minecraft:player] minecraft:resistance 200"

        lines.size assertsIs 7
    }

    function("attacker_sprint") {
        sprintCooldown.ifReady(attacker) {
            attacker.giveEffect(MobEffectArgument("speed"), duration = 60, amplifier = 2)
            sprintCooldown.start(attacker)
        }
        lines[0].startsWith("execute if score @e[limit=1,name=Attacker,team=blue,type=minecraft:player] sprint_boost matches 0 run function ctf:generated_scopes/cooldown_sprint_boost_ready_") assertsIs true
        lines.size assertsIs 1
    }

    function("on_combat") {
        attacker.onHurtEntity { say("Hit!") }
    }

    function("score_tracking") {
        scoreboard("captures") {
            create() assertsIs "scoreboard objectives add captures dummy"
        }
        attacker.setScore(
            "captures",
            1
        ) assertsIs "scoreboard players set @e[limit=1,name=Attacker,team=blue,type=minecraft:player] captures 1"
        lines.size assertsIs 2
    }

    function("overtime_check") {
        states.whenState("overtime") {
            say("Overtime! Next capture wins!")
        }
        lines[0].startsWith("execute if score #game_state kore_state matches 2 run function ctf:generated_scopes/state_overtime_handler_") assertsIs true
        lines.size assertsIs 1
    }

    function("end_round") {
        states.transitionTo("finished")
        lines[0] assertsIs "scoreboard players set #game_state kore_state 3"

        gameTimer.stop(attacker)
        lines[1] assertsIs "scoreboard players set @e[limit=1,name=Attacker,team=blue,type=minecraft:player] game_clock -1"

        bar.hide() assertsIs "bossbar set ctf:game_bar visible false"

        attacker.clearAllEffects() assertsIs "effect clear @e[limit=1,name=Attacker,team=blue,type=minecraft:player]"
        defender.clearAllEffects() assertsIs "effect clear @e[limit=1,name=Defender,team=red,type=minecraft:player]"

        attacker.showTitle("Round Over!", "Blue team wins!")
        lines[5] assertsIs """title @e[limit=1,name=Attacker,team=blue,type=minecraft:player] title "Round Over!""""
        lines[6] assertsIs """title @e[limit=1,name=Attacker,team=blue,type=minecraft:player] subtitle "Blue team wins!""""

        attacker.teleportTo(
            0,
            64,
            0
        ) assertsIs "teleport @e[limit=1,name=Attacker,team=blue,type=minecraft:player] 0 64 0"
        defender.teleportTo(
            0,
            64,
            0
        ) assertsIs "teleport @e[limit=1,name=Defender,team=red,type=minecraft:player] 0 64 0"

        lines.size assertsIs 9
    }

    generatedFunctions.any { it.name == OopConstants.timerInitFunctionName("game_clock") } assertsIs true
    generatedFunctions.any { it.name == OopConstants.cooldownInitFunctionName("sprint_boost") } assertsIs true
    generatedFunctions.any { it.name == OopConstants.spawnerSpawnFunctionName("flag_guard") } assertsIs true
    generatedFunctions.any { it.name == "kore_state_init" } assertsIs true
}.apply {
    generate()
}
