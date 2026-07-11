package io.github.ayfri.kore.timer

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.scores.score
import io.github.ayfri.kore.arguments.selector.scores
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.features.tags.functionTag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.commands.function as functionCommand

private val initializedTimers = mutableSetOf<String>()

/** Describes a timer that counts upward until it reaches [duration]. */
data class Timer(
    val duration: TimeNumber = 100.ticks,
    val name: String,
)

/** Exposes helper operations for a registered [Timer]. */
data class TimerHandle(val timer: Timer) {
	/** Runs [block] once [entity] reaches the configured timer duration, then stops the timer. */
    context(fn: Function)
    fun onComplete(entity: Entity, block: Function.() -> Unit) {
        val generated = fn.datapack.generatedFunction(
            OopConstants.timerCompleteFunctionName(timer.name, block.hashCode())
        ) {
            block()
            scoreboard {
                players {
                    set(entity.asSelector(), timer.name, -1)
                }
            }
        }

        fn.datapack.functionTag(OopConstants.timerCompleteHandlersTag, namespace = OopConstants.namespace) {
            this += generated.asId()
        }

        fn.execute {
            ifCondition {
                score(entity.asSelector(), timer.name, rangeOrInt(timer.duration.value.toInt()))
            }
            run { functionCommand(generated) }
        }
    }

	/** Starts counting this timer from `0` for [entity]. */
    context(fn: Function)
    fun start(entity: Entity) = fn.scoreboard {
        players {
            set(entity.asSelector(), timer.name, 0)
        }
    }

	/** Stops this timer for [entity] by setting its score to `-1`. */
    context(fn: Function)
    fun stop(entity: Entity) = fn.scoreboard {
        players {
            set(entity.asSelector(), timer.name, -1)
        }
    }
}

/** Registers a timer and its init/tick plumbing once per datapack. */
fun DataPack.registerTimer(timer: Timer): TimerHandle {
    val key = "$name:${timer.name}"
    if (key !in initializedTimers) {
        initializedTimers += key

        load(OopConstants.timerInitFunctionName(timer.name)) {
            scoreboard {
                objectives {
                    add(timer.name, ScoreboardCriteria.DUMMY)
                }
            }
        }

        tick(OopConstants.timerTickFunctionName(timer.name)) {
            scoreboard {
                players {
                    add(allPlayers {
                        scores {
                            score(timer.name, rangeOrIntStart(0))
                        }
                    }, timer.name, 1)
                }
            }
        }
    }

    return TimerHandle(timer)
}

/** Creates and registers a timer from a name and duration. */
fun DataPack.registerTimer(name: String, duration: TimeNumber = 100.ticks) =
    registerTimer(Timer(name = name, duration = duration))
