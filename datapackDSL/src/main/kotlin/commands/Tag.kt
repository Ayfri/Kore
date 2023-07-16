package commands

import arguments.types.EntityArgument
import arguments.types.literals.literal
import functions.Function

class Tag(private val fn: Function, val entity: EntityArgument) {
	fun add(tag: String) = fn.addLine(command("tag", entity, literal("add"), literal(tag)))
	fun list() = fn.addLine(command("tag", entity, literal("list")))
	fun remove(tag: String) = fn.addLine(command("tag", entity, literal("remove"), literal(tag)))
}

fun Function.tag(entity: EntityArgument, block: Tag.() -> Command) = Tag(this, entity).block()
