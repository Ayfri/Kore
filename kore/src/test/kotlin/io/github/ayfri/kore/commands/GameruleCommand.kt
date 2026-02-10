package io.github.ayfri.kore.commands

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Gamerules

fun Function.gameruleTests() {
	gamerule("minecraft:do_daylight_cycle", true) assertsIs "gamerule minecraft:do_daylight_cycle true"
	gamerule("minecraft:random_tick_speed") assertsIs "gamerule minecraft:random_tick_speed"

	gamerule(Gamerules.ADVANCE_TIME, true) assertsIs "gamerule minecraft:advance_time true"
	gamerule(Gamerules.RANDOM_TICK_SPEED, 3) assertsIs "gamerule minecraft:random_tick_speed 3"
	gamerule(Gamerules.RANDOM_TICK_SPEED) assertsIs "gamerule minecraft:random_tick_speed"
}
