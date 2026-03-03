package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.gamestate.registerGameStates
import io.github.ayfri.kore.utils.testDataPack

fun gameStateTests() = testDataPack("game_state_tests") {
    val states = registerGameStates {
        state("waiting")
        state("playing")
        state("ending")
    }

    states.stateByName("waiting").id assertsIs 0
    states.stateByName("playing").id assertsIs 1
    states.stateByName("ending").id assertsIs 2

    function("transition_to_playing") {
        states.transitionTo(states.stateByName("playing"))
        lines[0] assertsIs "scoreboard players set #game_state kore_state 1"
        lines.size assertsIs 1
    }

    function("transition_by_name") {
        states.transitionTo("ending")
        lines[0] assertsIs "scoreboard players set #game_state kore_state 2"
        lines.size assertsIs 1
    }

    function("when_playing") {
        states.whenState(states.stateByName("playing")) {
            say("Game is playing!")
        }
        lines[0].startsWith("execute if score #game_state kore_state matches 1 run function game_state_tests:generated_scopes/state_playing_handler_") assertsIs true
        lines.size assertsIs 1
    }

    generatedFunctions.any { it.name == "kore_state_init" } assertsIs true
}.apply {
    generate()
}
