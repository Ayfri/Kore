package commands

import functions.Function
import generated.Gamerules
import utils.assertsIs

fun Function.gameruleTests() {
	gamerule("doDaylightCycle", true) assertsIs "gamerule doDaylightCycle true"
	gamerule("randomTickSpeed") assertsIs "gamerule randomTickSpeed"

	gamerule(Gamerules.DO_DAYLIGHT_CYCLE, true) assertsIs "gamerule doDaylightCycle true"
	gamerule(Gamerules.RANDOM_TICK_SPEED, 3) assertsIs "gamerule randomTickSpeed 3"
	gamerule(Gamerules.RANDOM_TICK_SPEED) assertsIs "gamerule randomTickSpeed"
}
