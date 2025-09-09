package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.WaypointStyleArgument
import io.github.ayfri.kore.utils.asArg

data class WaypointModify(private val fn: Function, val selector: EntityArgument) {
	fun color(color: FormattingColor) = fn.addLine(command("waypoint", literal("modify"), selector, literal("color"), color))
	fun colorHex(color: Color) = fn.addLine(command("waypoint", literal("modify"), selector, literal("color"), literal("hex"), literal(color.toRGB().toHex())))
	fun colorReset() = fn.addLine(command("waypoint", literal("modify"), selector, literal("color"), literal("reset")))
	fun style(style: WaypointStyleArgument) = fn.addLine(command("waypoint", literal("modify"), selector, literal("style"), literal(style.asArg())))
	fun styleReset() = fn.addLine(command("waypoint", literal("modify"), selector, literal("style"), literal("reset")))
}

fun Function.waypointList() = addLine(command("waypoint", literal("list")))
fun Function.waypointModify(selector: EntityArgument, block: WaypointModify.() -> Command) = WaypointModify(this, selector).block()
