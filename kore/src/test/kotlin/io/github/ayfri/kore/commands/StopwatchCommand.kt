package io.github.ayfri.kore.commands

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.StopwatchArgument

fun Function.stopwatchTests() {
	val stopwatchId = StopwatchArgument("my_stopwatch")

	stopwatchCreate(stopwatchId) assertsIs "stopwatch create minecraft:my_stopwatch"
	stopwatchQuery(stopwatchId) assertsIs "stopwatch query minecraft:my_stopwatch"
	stopwatchQuery(stopwatchId, 20) assertsIs "stopwatch query minecraft:my_stopwatch 20"
	stopwatchRestart(stopwatchId) assertsIs "stopwatch restart minecraft:my_stopwatch"
	stopwatchRemove(stopwatchId) assertsIs "stopwatch remove minecraft:my_stopwatch"

	val stopWatchFn = stopWatch("my_stopwatch")
	stopWatchFn assertsIs "unit_tests:my_stopwatch"
}
