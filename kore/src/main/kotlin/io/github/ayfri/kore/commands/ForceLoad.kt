package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec2
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function

class ForceLoad(private val fn: Function) {
	fun add(from: Vec2, to: Vec2? = null) = fn.addLine(command("forceload", literal("add"), from, to))
	fun query(pos: Vec2? = null) = fn.addLine(command("forceload", literal("query"), pos))
	fun remove(from: Vec2, to: Vec2? = null) = fn.addLine(command("forceload", literal("remove"), from, to))
	fun removeAll() = fn.addLine(command("forceload", literal("remove"), literal("all")))
}

val Function.forceLoad get() = ForceLoad(this)
fun Function.forceLoad(block: ForceLoad.() -> Command) = ForceLoad(this).block()
