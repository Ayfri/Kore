package utils

import arguments.DisplaySlots
import arguments.chatcomponents.*
import arguments.chatcomponents.events.runCommand
import arguments.chatcomponents.events.suggestCommand
import arguments.colors.Color
import arguments.numbers.ranges.rangeOrIntStart
import arguments.selector.SelectorNbtData
import arguments.types.literals.allEntities
import arguments.types.literals.allPlayers
import arguments.types.literals.self
import commands.*
import commands.execute.execute
import commands.execute.run
import functions.Function
import generated.EntityTypes
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

	fun selector(block: SelectorNbtData.() -> Unit = {}) = allEntities(true) {
		tag = randomTag
		block()
	}

	fun whenAllTestsValid(block: Function.() -> Command) {
		whenAllTestsValid = block
	}

	fun whenAnyTestInvalid(block: Function.() -> Command) {
		whenAnyTestInvalid = block
	}

	context(Function)
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
		execute {
			ifCondition {
				entity(target)
			}

			run {
				whenTrue(key, value)
				scoreboard.objective(scoreName).add(selector, 1)
			}
		}

		execute {
			unlessCondition {
				entity(target)
			}

			run {
				whenFalse(key, value)
			}
		}
	}

	context(Function)
	fun assertAllData(whenFalse: DebugCallback? = null, whenTrue: DebugCallback? = null, data: MutableMap<String, String>.() -> Unit) {
		buildMap(data).forEach { (key, value) ->
			when {
				whenFalse != null && whenTrue != null -> assertData(key, value, whenFalse, whenTrue)
				whenFalse != null -> assertData(key, value, whenFalse)
				whenTrue != null -> assertData(key, value, whenTrue = whenTrue)
				else -> assertData(key, value)
			}
		}

		if (whenAllTestsValid != null) execute {
			ifCondition {
				score(selector, scoreName, rangeOrIntStart(scoreToValidateAllTests))
			}

			run {
				whenAllTestsValid!!()
			}
		}

		if (whenAnyTestInvalid != null) execute {
			unlessCondition {
				score(selector, scoreName, rangeOrIntStart(scoreToValidateAllTests))
			}

			run {
				whenAnyTestInvalid!!()
			}
		}
	}

	context(Function)
	fun displayDebugScore() {
		scoreboard.objective(scoreName).setDisplaySlot(DisplaySlots.sidebar)
	}

	context(Function)
	fun summon(): DebugEntity {
		summon(EntityTypes.MARKER) {
			putNbtCompound("data") {
				data.forEach { (key, value) ->
					put(key, value)
				}
			}

			putNbtList("Tags") {
				plus(randomTag)
			}
		}

		scoreboard.objectives.remove(scoreName)
		scoreboard.objectives.add(scoreName)

		return this@DebugEntity
	}

	context(Function)
	fun printTextToPrintData() {
		tellraw(allPlayers(), "Click here to print data") {
			color = Color.GOLD
			clickEvent {
				suggestCommand {
					execute {
						asTarget(self())
						run { data(selector).get() }
					}
				}
			}
		}

		tellraw(allPlayers(), "Click here to kill entity") {
			color = Color.DARK_RED
			clickEvent {
				runCommand {
					kill(selector)
				}
			}
		}
	}

	context(Function)
	fun remove() {
		kill(selector)
		scoreboard.objectives.remove(scoreName)
	}
}

context(DebugEntity)
private fun ChatComponent.setupDisplay(k: String) {
	clickEvent {
		runCommand {
			debug(textComponent("Real value: ") + nbtComponent("data.$k", selector) {
				color = Color.AQUA
			})
		}
	}

	hoverEvent("Click to see real value")
}

fun Function.debugEntity(block: MutableMap<String, String>.() -> Unit = {}) = DebugEntity(block).summon()
