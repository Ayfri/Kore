package io.github.ayfri.kore.commands

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Gamerules

fun Function.gameruleTests() {
	gamerule("doDaylightCycle", true) assertsIs "gamerule doDaylightCycle true"
	gamerule("randomTickSpeed") assertsIs "gamerule randomTickSpeed"

	gamerule(Gamerules.DO_DAYLIGHT_CYCLE, true) assertsIs "gamerule doDaylightCycle true"
	gamerule(Gamerules.RANDOM_TICK_SPEED, 3) assertsIs "gamerule randomTickSpeed 3"
	gamerule(Gamerules.RANDOM_TICK_SPEED) assertsIs "gamerule randomTickSpeed"
}
