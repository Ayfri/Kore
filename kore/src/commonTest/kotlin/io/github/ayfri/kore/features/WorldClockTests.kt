package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldclock.worldClock
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.worldClockTests() {
	worldClock("day")

	worldClocks.last() assertsIs """
		{}
	""".trimIndent()

	worldClock("season")

	worldClocks.last() assertsIs """
		{}
	""".trimIndent()
}

class WorldClockTests : FunSpec({
	test("world clock") {
		dataPack("worldClock") {
			pretty()
			worldClockTests()
		}
	}
})
