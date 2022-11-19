package commands

import arguments.Argument
import arguments.literal
import functions.Function

class Tag(private val fn: Function, val entity: Argument.Entity) {
	fun add(tag: String) = fn.addLine(command("tag", entity, literal("add"), literal(tag)))
	fun list() = fn.addLine(command("tag", entity, literal("list")))
	fun remove(tag: String) = fn.addLine(command("tag", entity, literal("remove"), literal(tag)))
}

fun Function.tag(entity: Argument.Entity, block: Tag.() -> Command) = Tag(this, entity).block()
