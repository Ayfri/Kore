package io.github.ayfri.kore.commands

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.StopwatchArgument

fun Function.stopwatchTests() {
	val stopwatchId = StopwatchArgument("my_stopwatch")

	stopwatchCreate(stopwatchId) assertsIs "stopwatch minecraft:my_stopwatch create"
	stopwatchQuery(stopwatchId) assertsIs "stopwatch minecraft:my_stopwatch query"
	stopwatchRestart(stopwatchId) assertsIs "stopwatch minecraft:my_stopwatch restart"
	stopwatchRemove(stopwatchId) assertsIs "stopwatch minecraft:my_stopwatch remove"

	val stopWatchFn = stopWatch("my_stopwatch")
	stopWatchFn assertsIs "unit_tests:my_stopwatch"
}
