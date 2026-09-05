package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function

/** DSL scope for the `/tag` command bound to [entity]. */
class Tag(private val fn: Function, val entity: EntityArgument) {
	/** Adds [tag] to [entity]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tag) */
	fun add(tag: String) = fn.addLine(command("tag", entity, literal("add"), literal(tag)))
	/** Lists every tag currently attached to [entity]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tag) */
	fun list() = fn.addLine(command("tag", entity, literal("list")))
	/** Removes [tag] from [entity]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tag) */
	fun remove(tag: String) = fn.addLine(command("tag", entity, literal("remove"), literal(tag)))
}

/** Opens the [Tag] DSL for [entity]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tag) */
fun Function.tag(entity: EntityArgument, block: Tag.() -> Command) = Tag(this, entity).block()
