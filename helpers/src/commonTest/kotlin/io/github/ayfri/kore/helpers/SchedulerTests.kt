package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.kotest.core.spec.style.FunSpec

private fun Function.isSchedulerWrapper() = name.startsWith("scheduler_") && name != "scheduler_setup" && !name.startsWith("scheduler_task_")

fun DataPack.schedulerTest() {
	schedulerManager {
		debug = true

		addScheduler(1.2.seconds) {
			debug("Hello World deferred 1.2 second")
		}

		addScheduler(3.seconds, 1.2.seconds) {
			debug("Hello World deferred 3 seconds, repeating every 1.2 seconds")
		}

		addScheduler(8.seconds) {
			unScheduleAll()
			debug("Hello World deferred 8 seconds, cleared all schedulers")
		}
	}
}

class SchedulerTests : FunSpec({
	test("scheduler") {
		dataPack("helpers_tests") {
			schedulerTest()
		}
	}

	test("repeating scheduler reschedules its own wrapper") {
		dataPack("scheduler_repeat") {
			schedulerManager {
				addScheduler(3.seconds, 1.seconds) { say("tick") }
			}

			val wrapper = generatedFunctions.single { it.isSchedulerWrapper() }
			wrapper.lines assertsIs listOf("say tick", "schedule function ${wrapper.asId()} 1s replace")
			generatedFunctions.single { it.name == "scheduler_setup" }.lines assertsIs listOf("schedule function ${wrapper.asId()} 3s replace")
		}
	}

	test("one-shot scheduler generates no wrapper") {
		dataPack("scheduler_once") {
			schedulerManager {
				addScheduler(3.seconds) { say("once") }
			}

			generatedFunctions.none { it.isSchedulerWrapper() } assertsIs true
		}
	}
})
