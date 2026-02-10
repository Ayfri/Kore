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

class WorldBorder(private val fn: Function) {
	fun add(distance: Double, time: Int? = null) = fn.addLine(command("worldborder", literal("add"), float(distance), int(time)))
	fun add(distance: Double, time: TimeNumber) = fn.addLine(command("worldborder", literal("add"), float(distance), time(time)))
	fun center(pos: Vec2) = fn.addLine(command("center", pos))
	fun center(x: Double, z: Double) = fn.addLine(command("worldborder", literal("center"), float(x), float(z)))
	fun center(x: PosNumber, z: PosNumber) = fn.addLine(command("worldborder", literal("center"), literal(x.asArg()), literal(z.asArg())))
	fun damageAmount(amount: Float) = fn.addLine(command("worldborder", literal("damage"), literal("amount"), float(amount)))
	fun damageBuffer(distance: Double) = fn.addLine(command("worldborder", literal("damage"), literal("buffer"), float(distance)))
	fun damagePerBlock(amount: Float) = damageAmount(amount)
	fun get() = fn.addLine(command("worldborder", literal("get")))
	fun set(distance: Double, time: Int? = null) = fn.addLine(command("worldborder", literal("set"), float(distance), int(time)))
	fun set(distance: Double, time: TimeNumber) = fn.addLine(command("worldborder", literal("set"), float(distance), time(time)))
	fun setWarningDistance(distance: Int) = fn.addLine(command("worldborder", literal("warning"), literal("distance"), int(distance)))
	fun setWarningTime(time: Int) = fn.addLine(command("worldborder", literal("warning"), literal("time"), int(time)))
	fun setWarningTime(time: TimeNumber) = fn.addLine(command("worldborder", literal("warning"), literal("time"), time(time)))
}

val Function.worldBorder get() = WorldBorder(this)
fun Function.worldBorder(block: WorldBorder.() -> Command) = WorldBorder(this).block()
fun Function.worldBorder() = worldBorder.get()
