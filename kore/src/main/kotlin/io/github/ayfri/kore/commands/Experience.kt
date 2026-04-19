package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.Xp
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function

/** DSL scope for the `/experience` command bound to [target]. */
class Experience(private val fn: Function, val target: EntityArgument) {
	/** Adds [amount] experience to [target]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/experience) */
	fun add(amount: Xp) = fn.addLine(command("experience", literal("add"), target, literal(amount.toString())))
	/** Queries how many levels [target] has. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/experience) */
	fun queryLevels() = fn.addLine(command("experience", literal("query"), target, literal("levels")))
	/** Queries how many experience points [target] has. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/experience) */
	fun queryPoints() = fn.addLine(command("experience", literal("query"), target, literal("points")))
	/** Removes [amount] experience from [target]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/experience) */
	fun remove(amount: Xp) = add(-amount)
	/** Sets [target]'s experience to [amount]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/experience) */
	fun set(amount: Xp) = fn.addLine(command("experience", literal("set"), target, literal(amount.toString())))

	operator fun plusAssign(amount: Xp) {
		add(amount)
	}

	operator fun minusAssign(amount: Xp) {
		remove(amount)
	}
}

/** Returns the reusable [Experience] DSL. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/experience) */
fun Function.experience(target: EntityArgument) = Experience(this, target)
/** Opens the [Experience] DSL. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/experience) */
fun Function.experience(target: EntityArgument, block: Experience.() -> Command) = Experience(this, target).block()
/** Alias for [experience]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/xp) */
fun Function.xp(target: EntityArgument) = Experience(this, target)
/** Alias for [experience]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/xp) */
fun Function.xp(target: EntityArgument, block: Experience.() -> Command) = Experience(this, target).block()
