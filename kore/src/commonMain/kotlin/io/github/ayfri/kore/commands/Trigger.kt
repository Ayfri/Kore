package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function

/**
 * Builder for the `trigger` command.
 *
 * `trigger` is the only scoreboard-related command available to non-op players and requires the
 * objective to use the `trigger` criterion and to have been enabled for the player.
 */
class Trigger(private val fn: Function, val objective: String) {
	/** Triggers the objective with its default behaviour: increments by 1. */
	fun trigger() = fn.addLine(command("trigger", literal(objective)))

	/** Increments the trigger score by [value]. */
	fun add(value: Int) = fn.addLine(command("trigger", literal(objective), literal("add"), int(value)))

	/** Sets the trigger score to [value] (overwrites the current value). */
	fun set(value: Int) = fn.addLine(command("trigger", literal(objective), literal("set"), int(value)))

	/** Decrements the trigger score by [value] (sugar for [add] with a negative argument). */
	fun remove(value: Int) = add(-value)

	operator fun plusAssign(value: Int) {
		add(value)
	}

	operator fun minusAssign(value: Int) {
		add(-value)
	}
}

/** Triggers [objective] with the default increment-by-1 behaviour. */
fun Function.trigger(objective: String) = addLine(command("trigger", literal(objective)))

/** Enters a [Trigger] DSL block for [objective]. */
fun Function.trigger(objective: String, block: Trigger.() -> Command) = Trigger(this, objective).block()
