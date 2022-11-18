package commands

import arguments.Argument
import arguments.literal
import functions.Function

class Tag(private val fn: Function, val entity: Argument.Entity) {
	fun add(tag: String) = fn.addLine(command("tag", entity, fn.literal("add"), fn.literal(tag)))
	fun list() = fn.addLine(command("tag", entity, fn.literal("list")))
	fun remove(tag: String) = fn.addLine(command("tag", entity, fn.literal("remove"), fn.literal(tag)))
}

fun Function.tag(entity: Argument.Entity, block: Tag.() -> Unit) = Tag(this, entity).block()
