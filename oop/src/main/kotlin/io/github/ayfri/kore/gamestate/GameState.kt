package io.github.ayfri.kore.gamestate

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.features.tags.functionTag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.commands.function as functionCommand

private val initializedStates = mutableSetOf<String>()

data class GameState(val id: Int, val name: String)

data class GameStateManager(val states: List<GameState>) {
    fun stateByName(name: String) = states.first { it.name == name }

    context(fn: Function)
    fun transitionTo(state: GameState) = fn.scoreboard {
        players {
            set(literal(OopConstants.stateHolder), OopConstants.stateObjective, state.id)
        }
    }

    context(fn: Function)
    fun transitionTo(stateName: String) = transitionTo(stateByName(stateName))

    context(fn: Function)
    fun whenState(state: GameState, block: Function.() -> Unit) {
        val generated = fn.datapack.generatedFunction(
            OopConstants.stateHandlerName(state.name, block.hashCode())
        ) {
            block()
        }

        fn.datapack.functionTag(OopConstants.stateWhenHandlersTag, namespace = OopConstants.namespace) {
            this += generated.asId()
        }

        fn.execute {
            ifCondition {
                score(literal(OopConstants.stateHolder), OopConstants.stateObjective, rangeOrInt(state.id))
            }
            run { functionCommand(generated) }
        }
    }

    context(fn: Function)
    fun whenState(stateName: String, block: Function.() -> Unit) = whenState(stateByName(stateName), block)
}

class GameStateManagerBuilder {
    private val states = mutableListOf<GameState>()
    private var nextId = 0

    fun build() = GameStateManager(states.toList())

    fun state(name: String, id: Int = nextId): GameState {
        val state = GameState(id, name)
        states += state
        nextId = maxOf(nextId, id + 1)
        return state
    }
}

fun DataPack.registerGameStates(block: GameStateManagerBuilder.() -> Unit): GameStateManager {
    val manager = GameStateManagerBuilder().apply(block).build()

    val key = "$name:game_state"
    if (key !in initializedStates) {
        initializedStates += key

        load(OopConstants.stateInitFunction) {
            scoreboard {
                objectives {
                    add(OopConstants.stateObjective, ScoreboardCriteria.DUMMY)
                }
                players {
                    set(literal(OopConstants.stateHolder), OopConstants.stateObjective, manager.states.first().id)
                }
            }
        }
    }

    return manager
}
