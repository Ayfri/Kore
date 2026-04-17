package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.WaypointStyleArgument
import io.github.ayfri.kore.utils.asArg

/**
 * DSL scope for the `waypoint modify <selector> …` command family.
 *
 * See [Minecraft wiki: waypoint](https://minecraft.wiki/w/Commands/waypoint) for the underlying grammar.
 */
data class WaypointModify(private val fn: Function, val selector: EntityArgument) {
	/** Sets the waypoint icon color to a named [FormattingColor]. */
	fun color(color: FormattingColor) = fn.addLine(command("waypoint", literal("modify"), selector, literal("color"), color))

	/** Sets the waypoint icon color to a custom hex [color]. */
	fun colorHex(color: Color) = fn.addLine(command("waypoint", literal("modify"), selector, literal("color"), literal("hex"), literal(color.toRGB().toHex())))

	/** Clears any custom waypoint color. */
	fun colorReset() = fn.addLine(command("waypoint", literal("modify"), selector, literal("color"), literal("reset")))

	/** Sets the waypoint icon style using a [WaypointStyleArgument] registry reference. */
	fun style(style: WaypointStyleArgument) = fn.addLine(
		command(
			"waypoint",
			literal("modify"),
			selector,
			literal("style"),
			literal("set"),
			literal(style.asArg())
		)
	)

	/** Clears any custom waypoint style. */
	fun styleReset() = fn.addLine(command("waypoint", literal("modify"), selector, literal("style"), literal("reset")))
}

/** Lists every waypoint currently registered. */
fun Function.waypointList() = addLine(command("waypoint", literal("list")))

/** Opens the [WaypointModify] DSL scope for the given [selector]. */
fun Function.waypointModify(selector: EntityArgument, block: WaypointModify.() -> Command) = WaypointModify(this, selector).block()
