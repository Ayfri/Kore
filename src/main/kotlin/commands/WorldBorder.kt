package commands

import arguments.float
import arguments.int
import arguments.literal
import arguments.numbers.PosNumber
import functions.Function

class WorldBorder(private val fn: Function) {
	fun add(distance: Double, time: Int? = null) = fn.addLine(command("worldborder", fn.literal("add"), fn.float(distance), fn.int(time)))
	fun center(x: Double, z: Double) = fn.addLine(command("worldborder", fn.literal("center"), fn.float(x), fn.float(z)))
	fun center(x: PosNumber, z: PosNumber) = fn.addLine(command("worldborder", fn.literal("center"), fn.literal(x.asArg()), fn.literal(z.asArg())))
	fun damageBuffer(distance: Double) = fn.addLine(command("worldborder", fn.literal("damage"), fn.literal("buffer"), fn.float(distance)))
	fun damagePerBlock(amount: Float) = fn.addLine(command("worldborder", fn.literal("damage"), fn.literal("amount"), fn.float(amount)))
	fun get() = fn.addLine(command("worldborder", fn.literal("get")))
	fun set(distance: Double, time: Int? = null) = fn.addLine(command("worldborder", fn.literal("set"), fn.float(distance), fn.int(time)))
	fun setWarningDistance(distance: Int) = fn.addLine(command("worldborder", fn.literal("warning"), fn.literal("distance"), fn.int(distance)))
	fun setWarningTime(time: Int) = fn.addLine(command("worldborder", fn.literal("warning"), fn.literal("time"), fn.int(time)))
}

val Function.worldBorder get() = WorldBorder(this)
fun Function.worldBorder(block: WorldBorder.() -> Unit) = WorldBorder(this).block()
fun Function.worldBorder() = worldBorder.get()
