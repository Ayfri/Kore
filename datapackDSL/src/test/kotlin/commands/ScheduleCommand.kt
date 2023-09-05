package commands

import arguments.numbers.days
import arguments.numbers.seconds
import arguments.numbers.ticks
import assertions.assertsIs
import functions.Function

fun Function.scheduleTests() {
	schedule("test").append(1.seconds) assertsIs "schedule function test 1s append"
	schedule("test").clear() assertsIs "schedule clear test"
	schedule(this).replace(1.days) assertsIs "schedule function ${asString()} 1d replace"
	schedules {
		append("test", 1.ticks) assertsIs "schedule function test 1 append"
		clear("test") assertsIs "schedule clear test"
		replace("test", 1.ticks) assertsIs "schedule function test 1 replace"
	}
}
