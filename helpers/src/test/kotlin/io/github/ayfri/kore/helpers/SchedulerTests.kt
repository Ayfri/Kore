package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.dataPack
import io.kotest.core.spec.style.FunSpec

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
			path = kotlinx.io.files.Path("out")
			schedulerTest()
		}.generate()
	}
})
