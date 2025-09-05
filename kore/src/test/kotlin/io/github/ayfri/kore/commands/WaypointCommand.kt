package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.waypointTests() {
	waypointList() assertsIs "waypoint list"
	waypointModify(self()) {
		color(Color.RED) assertsIs "waypoint modify @s color red"
		colorHex(Color.DARK_PURPLE) assertsIs "waypoint modify @s color hex aa00aa"
		colorReset() assertsIs "waypoint modify @s color reset"
		fade(0, 0, 100, 255) assertsIs "waypoint modify @s fade 0 0 100 255"
		fadeReset() assertsIs "waypoint modify @s fade reset"
	}
}
