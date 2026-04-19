package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec2
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function

/** DSL scope for the `/forceload` command. */
class ForceLoad(private val fn: Function) {
	/** Force-loads the chunk at [from], or the range from [from] to [to]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/forceload) */
	fun add(from: Vec2, to: Vec2? = null) = fn.addLine(
		command(
			"forceload",
			literal("add"),
			literal(from.toStringTruncated()),
			literal(to?.toStringTruncated())
		)
	)

	/** Checks whether the chunk at [pos] is force-loaded. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/forceload) */
	fun query(pos: Vec2? = null) = fn.addLine(command("forceload", literal("query"), literal(pos?.toStringTruncated())))
	/** Stops force-loading the chunk at [from], or the range from [from] to [to]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/forceload) */
	fun remove(from: Vec2, to: Vec2? = null) = fn.addLine(
		command(
			"forceload",
			literal("remove"),
			literal(from.toStringTruncated()),
			literal(to?.toStringTruncated())
		)
	)
	/** Removes every force-loaded chunk. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/forceload) */
	fun removeAll() = fn.addLine(command("forceload", literal("remove"), literal("all")))
}

/** Returns the reusable [ForceLoad] DSL. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/forceload) */
val Function.forceLoad get() = ForceLoad(this)
/** Opens the [ForceLoad] DSL. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/forceload) */
fun Function.forceLoad(block: ForceLoad.() -> Command) = ForceLoad(this).block()
