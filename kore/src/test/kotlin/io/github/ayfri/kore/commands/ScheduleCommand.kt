package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.days
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.kotest.core.spec.style.FunSpec

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

class ScheduleCommandTests : FunSpec({
	test("schedule") {
		dataPack("unit_tests") {
			load { scheduleTests() }
		}.generate()
	}
})
