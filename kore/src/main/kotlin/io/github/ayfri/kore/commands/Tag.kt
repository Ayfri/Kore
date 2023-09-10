package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function

class Tag(private val fn: Function, val entity: EntityArgument) {
	fun add(tag: String) = fn.addLine(command("tag", entity, literal("add"), literal(tag)))
	fun list() = fn.addLine(command("tag", entity, literal("list")))
	fun remove(tag: String) = fn.addLine(command("tag", entity, literal("remove"), literal(tag)))
}

fun Function.tag(entity: EntityArgument, block: Tag.() -> Command) = Tag(this, entity).block()
