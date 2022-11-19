package commands

import arguments.float
import arguments.int
import arguments.literal
import arguments.numbers.PosNumber
import functions.Function

class WorldBorder(private val fn: Function) {
	fun add(distance: Double, time: Int? = null) = fn.addLine(command("worldborder", literal("add"), float(distance), int(time)))
	fun center(x: Double, z: Double) = fn.addLine(command("worldborder", literal("center"), float(x), float(z)))
	fun center(x: PosNumber, z: PosNumber) = fn.addLine(command("worldborder", literal("center"), literal(x.asArg()), literal(z.asArg())))
	fun damageBuffer(distance: Double) = fn.addLine(command("worldborder", literal("damage"), literal("buffer"), float(distance)))
	fun damagePerBlock(amount: Float) = fn.addLine(command("worldborder", literal("damage"), literal("amount"), float(amount)))
	fun get() = fn.addLine(command("worldborder", literal("get")))
	fun set(distance: Double, time: Int? = null) = fn.addLine(command("worldborder", literal("set"), float(distance), int(time)))
	fun setWarningDistance(distance: Int) = fn.addLine(command("worldborder", literal("warning"), literal("distance"), int(distance)))
	fun setWarningTime(time: Int) = fn.addLine(command("worldborder", literal("warning"), literal("time"), int(time)))
}

val Function.worldBorder get() = WorldBorder(this)
fun Function.worldBorder(block: WorldBorder.() -> Unit) = WorldBorder(this).block()
fun Function.worldBorder() = worldBorder.get()
