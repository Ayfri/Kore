package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.time
import io.github.ayfri.kore.functions.Function

/**
 * DSL scope for the `/tick` command.
 *
 * `/tick` can freeze the game, advance it one step at a time, sprint through ticks, and adjust
 * the target tick rate.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tick)
 */
class Tick(private val fn: Function) {
	/**
	 * Freezes ticking. Players and ridden entities keep ticking.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tick)
	 */
	fun freeze() = fn.addLine(command("tick", literal("freeze")))

	/**
	 * Reports the current tick state and target tick rate.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tick)
	 */
	fun query() = fn.addLine(command("tick", literal("query")))

	/**
	 * Sets the target tick rate in TPS.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tick)
	 */
	fun rate(rate: Float) = fn.addLine(command("tick", literal("rate"), float(rate)))

	/**
	 * Starts tick sprinting for [time] ticks.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tick)
	 */
	fun sprint(time: Int) = fn.addLine(command("tick", literal("sprint"), int(time)))

	/**
	 * Starts tick sprinting for a [TimeNumber] duration.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tick)
	 */
	fun sprint(time: TimeNumber) = fn.addLine(command("tick", literal("sprint"), time(time)))

	/**
	 * Stops the active tick sprint and restores the previous ticking state.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tick)
	 */
	fun sprintStop() = fn.addLine(command("tick", literal("sprint"), literal("stop")))

	/**
	 * Stops the current stepping process and refreezes ticking.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tick)
	 */
	fun stepStop() = fn.addLine(command("tick", literal("step"), literal("stop")))

	/**
	 * Steps a single tick while frozen.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tick)
	 */
	fun step() = fn.addLine(command("tick", literal("step")))

	/**
	 * Steps [time] ticks while frozen.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tick)
	 */
	fun step(time: Int) = fn.addLine(command("tick", literal("step"), int(time)))

	/**
	 * Steps a [TimeNumber] duration while frozen.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tick)
	 */
	fun step(time: TimeNumber) = fn.addLine(command("tick", literal("step"), time(time)))

	/**
	 * Unfreezes ticking.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tick)
	 */
	fun unfreeze() = fn.addLine(command("tick", literal("unfreeze")))
}

/** Returns the reusable [Tick] DSL. */
val Function.tick get() = Tick(this)

/** Opens the [Tick] DSL. */
fun Function.tick(block: Tick.() -> Command) = Tick(this).block()
