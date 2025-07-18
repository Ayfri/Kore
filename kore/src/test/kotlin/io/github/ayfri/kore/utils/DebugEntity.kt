package io.github.ayfri.kore.utils

import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.chatcomponents.*
import io.github.ayfri.kore.arguments.chatcomponents.events.runCommand
import io.github.ayfri.kore.arguments.chatcomponents.events.suggestCommand
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.*
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.putNbtCompound
import net.benwoodworth.knbt.putNbtList
import java.util.*

typealias DebugCallback = Function.(key: String, value: String) -> Command

data class DebugEntity(val data: Map<String, String> = mutableMapOf()) {
	constructor(block: MutableMap<String, String>.() -> Unit) : this(buildMap(block))

	val randomTag = UUID.randomUUID().toString()
	var whenAllTestsValid: (Function.() -> Command)? = null
	var whenAnyTestInvalid: (Function.() -> Command)? = null
	val selector get() = selector()
	val scoreName get() = "$randomTag.score"
	val scoreToValidateAllTests get() = data.size

	fun selector(block: SelectorArguments.() -> Unit = {}) = allEntities(true) {
		tag = randomTag
		block()
	}

	fun whenAllTestsValid(block: Function.() -> Command) {
		whenAllTestsValid = block
	}

	fun whenAnyTestInvalid(block: Function.() -> Command) {
		whenAnyTestInvalid = block
	}

	context(fn: Function)
	fun assertData(
		key: String, value: String,
		whenFalse: DebugCallback = { k, v ->
			debug("assertData failed: $k != $v") {
				color = Color.RED
				setupDisplay(k)
			}
		},
		whenTrue: DebugCallback = { k, v ->
			debug("assertData passed: $k == $v") {
				color = Color.GREEN
				setupDisplay(k)
			}
		},
	) {
		val target = selector { nbt = nbt { putNbtCompound("data") { put(key, value) } } }
		fn.execute {
			ifCondition {
				entity(target)
			}

			run {
				whenTrue(key, value)
				scoreboard.objective(scoreName).add(selector, 1)
			}
		}

		fn.execute {
			unlessCondition {
				entity(target)
			}

			run {
				whenFalse(key, value)
			}
		}
	}

	context(fn: Function)
	fun assertAllData(whenFalse: DebugCallback? = null, whenTrue: DebugCallback? = null, data: MutableMap<String, String>.() -> Unit) {
		buildMap(data).forEach { (key, value) ->
			when {
				whenFalse != null && whenTrue != null -> assertData(key, value, whenFalse, whenTrue)
				whenFalse != null -> assertData(key, value, whenFalse)
				whenTrue != null -> assertData(key, value, whenTrue = whenTrue)
				else -> assertData(key, value)
			}
		}

		if (whenAllTestsValid != null) fn.execute {
			ifCondition {
				score(selector, scoreName, rangeOrIntStart(scoreToValidateAllTests))
			}

			run {
				whenAllTestsValid!!()
			}
		}

		if (whenAnyTestInvalid != null) fn.execute {
			unlessCondition {
				score(selector, scoreName, rangeOrIntStart(scoreToValidateAllTests))
			}

			run {
				whenAnyTestInvalid!!()
			}
		}
	}

	context(fn: Function)
	fun displayDebugScore() {
		fn.scoreboard.objective(scoreName).setDisplaySlot(DisplaySlots.sidebar)
	}

	context(fn: Function)
	fun summon(): DebugEntity {
		fn.summon(EntityTypes.MARKER) {
			putNbtCompound("data") {
				data.forEach { (key, value) ->
					put(key, value)
				}
			}

			putNbtList("Tags") {
				plus(randomTag)
			}
		}

		fn.scoreboard.objectives.remove(scoreName)
		fn.scoreboard.objectives.add(scoreName)

		return this@DebugEntity
	}

	context(fn: Function)
	fun printTextToPrintData() {
		fn.tellraw(allPlayers(), "Click here to print data") {
			color = Color.GOLD
			clickEvent {
				suggestCommand {
					fn.execute {
						asTarget(self())
						run { data(selector).get() }
					}
				}
			}
		}

		fn.tellraw(allPlayers(), "Click here to kill entity") {
			color = Color.DARK_RED
			clickEvent {
				runCommand {
					fn.kill(selector)
				}
			}
		}
	}

	context(fn: Function)
	fun remove() {
		fn.kill(selector)
		fn.scoreboard.objectives.remove(scoreName)
	}
}

context(entity: DebugEntity)
private fun ChatComponent.setupDisplay(k: String) {
	clickEvent {
		runCommand {
			debug(textComponent("Real value: ") + nbtComponent("data.$k", entity.selector) {
				color = Color.AQUA
			})
		}
	}

	hoverEvent("Click to see real value")
}

fun Function.debugEntity(block: MutableMap<String, String>.() -> Unit = {}) = DebugEntity(block).summon()
