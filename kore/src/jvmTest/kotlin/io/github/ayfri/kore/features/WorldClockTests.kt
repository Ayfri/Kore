package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldclock.worldClock
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
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
		testDataPack("worldClock") {
			pretty()
			worldClockTests()
		}.apply {
			assertGeneratorsGenerated()
			generate()
		}
	}
})
