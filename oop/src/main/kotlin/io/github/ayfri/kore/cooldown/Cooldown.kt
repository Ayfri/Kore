package io.github.ayfri.kore.cooldown

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

private val initializedCooldowns = mutableSetOf<String>()

data class Cooldown(
	val duration: TimeNumber = 20.ticks,
    val name: String,
)

data class CooldownHandle(val cooldown: Cooldown) {
	context(fn: Function)
	fun ifReady(entity: Entity, block: Function.() -> Unit) {
		val generated = fn.datapack.generatedFunction(
            OopConstants.cooldownReadyHandlerName(cooldown.name, block.hashCode())
		) {
			block()
			scoreboard {
				players {
					set(entity.asSelector(), cooldown.name, cooldown.duration.value.toInt())
				}
			}
		}

        fn.datapack.functionTag(OopConstants.cooldownReadyHandlersTag, namespace = OopConstants.namespace) {
            this += generated.asId()
        }

		fn.execute {
			ifCondition {
				score(entity.asSelector(), cooldown.name, rangeOrInt(0))
			}
			run { functionCommand(generated) }
		}
	}

    context(fn: Function)
    fun reset(entity: Entity) = fn.scoreboard {
        players {
            set(entity.asSelector(), cooldown.name, 0)
        }
    }

    context(fn: Function)
    fun start(entity: Entity) = fn.scoreboard {
        players {
            set(entity.asSelector(), cooldown.name, cooldown.duration.value.toInt())
        }
    }
}

fun DataPack.registerCooldown(cooldown: Cooldown): CooldownHandle {
	val key = "$name:${cooldown.name}"
	if (key !in initializedCooldowns) {
		initializedCooldowns += key

        load(OopConstants.cooldownInitFunctionName(cooldown.name)) {
			scoreboard {
				objectives {
					add(cooldown.name, ScoreboardCriteria.DUMMY)
				}
			}
		}

        tick(OopConstants.cooldownTickFunctionName(cooldown.name)) {
			scoreboard {
				players {
					remove(allPlayers {
						scores {
							score(cooldown.name, rangeOrIntStart(1))
						}
					}, cooldown.name, 1)
				}
			}
		}
	}

	return CooldownHandle(cooldown)
}

fun DataPack.registerCooldown(name: String, duration: TimeNumber = 20.ticks) =
    registerCooldown(Cooldown(name = name, duration = duration))
