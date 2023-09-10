package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.numbers.seconds

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
