package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec2
import io.github.ayfri.kore.arguments.numbers.PosNumber
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.time
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.utils.asArg

/**
 * Exposes the `worldborder` command.
 *
 * See the [Minecraft wiki](https://minecraft.wiki/w/Commands/worldborder) for details on each subcommand.
 */
class WorldBorder(private val fn: Function) {
	/** Increases or decreases the border size by [distance] blocks, optionally over [time] ticks. */
	fun add(distance: Double, time: Int? = null) = fn.addLine(command("worldborder", literal("add"), float(distance), int(time)))

	/** Increases or decreases the border size by [distance] blocks over the given [time]. */
	fun add(distance: Double, time: TimeNumber) = fn.addLine(command("worldborder", literal("add"), float(distance), time(time)))

	/** Recenters the world border on the given [pos]. */
	fun center(pos: Vec2) = fn.addLine(command("worldborder", literal("center"), pos))

	/** Recenters the world border on the given [x]/[z] coordinates. */
	fun center(x: Double, z: Double) = fn.addLine(command("worldborder", literal("center"), float(x), float(z)))

	/** Recenters the world border using [PosNumber] coordinates (supports relative/local positions). */
	fun center(x: PosNumber, z: PosNumber) = fn.addLine(command("worldborder", literal("center"), literal(x.asArg()), literal(z.asArg())))

	/** Sets damage dealt per block outside the border. */
	fun damageAmount(amount: Float) = fn.addLine(command("worldborder", literal("damage"), literal("amount"), float(amount)))

	/** Sets the safe buffer distance outside the border. */
	fun damageBuffer(distance: Double) = fn.addLine(command("worldborder", literal("damage"), literal("buffer"), float(distance)))

	/** Alias of [damageAmount]. */
	fun damagePerBlock(amount: Float) = damageAmount(amount)

	/** Queries the current border diameter. */
	fun get() = fn.addLine(command("worldborder", literal("get")))

	/** Sets the border diameter, optionally interpolating over [time] ticks. */
	fun set(distance: Double, time: Int? = null) = fn.addLine(command("worldborder", literal("set"), float(distance), int(time)))

	/** Sets the border diameter, interpolating over the given [time]. */
	fun set(distance: Double, time: TimeNumber) = fn.addLine(command("worldborder", literal("set"), float(distance), time(time)))

	/** Sets the warning distance in blocks from the border. */
	fun setWarningDistance(distance: Int) = fn.addLine(command("worldborder", literal("warning"), literal("distance"), int(distance)))

	/** Sets the warning time in ticks before the border reaches a player. */
	fun setWarningTime(time: Int) = fn.addLine(command("worldborder", literal("warning"), literal("time"), int(time)))

	/** Sets the warning time using a [TimeNumber] (seconds/days/ticks). */
	fun setWarningTime(time: TimeNumber) = fn.addLine(command("worldborder", literal("warning"), literal("time"), time(time)))
}

val Function.worldBorder get() = WorldBorder(this)
fun Function.worldBorder(block: WorldBorder.() -> Command) = WorldBorder(this).block()
fun Function.worldBorder() = worldBorder.get()
