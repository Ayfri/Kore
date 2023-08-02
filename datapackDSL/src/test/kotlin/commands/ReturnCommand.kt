package commands

import functions.Function
import functions.function
import utils.assertsIs

fun Function.returnCommand() {
	val function = datapack.function("test") {
		say("test")
		returnValue(10)
	}

	returnValue(0) assertsIs "return 0"
	returnRun(function) assertsIs "return run ${datapack.name}:test"
}
