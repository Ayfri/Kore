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
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.commands.function as functionCommand

private val initializedCooldowns = mutableSetOf<String>()

/** A scoreboard-based cooldown identified by [name] with the given [duration]. */
data class Cooldown(
	val name: String,
	val duration: TimeNumber = 20.ticks,
)

fun cooldown(name: String, duration: TimeNumber = 20.ticks) = Cooldown(name, duration)

/** Handle returned by [registerCooldown] that provides start/reset/ifReady operations. */
class CooldownHandle(val cooldown: Cooldown) {
	context(fn: Function)
	fun start(entity: Entity) {
		fn.scoreboard {
			players {
				set(entity.asSelector(), cooldown.name, cooldown.duration.value.toInt())
			}
		}
	}

	context(fn: Function)
	fun reset(entity: Entity) {
		fn.scoreboard {
			players {
				set(entity.asSelector(), cooldown.name, 0)
			}
		}
	}

	context(fn: Function)
	fun ifReady(entity: Entity, block: Function.() -> Unit) {
		val generated = fn.datapack.generatedFunction(
			OopConstants.Cooldown.readyHandlerName(cooldown.name, block.hashCode())
		) {
			block()
			scoreboard {
				players {
					set(entity.asSelector(), cooldown.name, cooldown.duration.value.toInt())
				}
			}
		}

		fn.execute {
			ifCondition {
				score(entity.asSelector(), cooldown.name, rangeOrInt(0))
			}
			run { functionCommand(generated) }
		}
	}
}

/**
 * Registers a cooldown and returns a [CooldownHandle].
 *
 * Generates a load function (objective creation) and a tick function (decrement every tick).
 */
fun DataPack.registerCooldown(cooldown: Cooldown): CooldownHandle {
	val key = "$name:${cooldown.name}"
	if (key !in initializedCooldowns) {
		initializedCooldowns += key

		load(OopConstants.Cooldown.initFunctionName(cooldown.name)) {
			scoreboard {
				objectives {
					add(cooldown.name, ScoreboardCriteria.DUMMY)
				}
			}
		}

		tick(OopConstants.Cooldown.tickFunctionName(cooldown.name)) {
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
	registerCooldown(Cooldown(name, duration))
