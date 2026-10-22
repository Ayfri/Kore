package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.numbers.days
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.numbers.ticks
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TimeNumberTests : FunSpec({
	test("wholeTicks converts every unit to ticks") {
		20.ticks.wholeTicks() shouldBe 20
		2.seconds.wholeTicks() shouldBe 40
		1.days.wholeTicks() shouldBe 24000
	}

	test("wholeTicks truncates a fraction of a tick") {
		0.07.seconds.wholeTicks() shouldBe 1
		0.049.seconds.wholeTicks() shouldBe 0
		1.9.ticks.wholeTicks() shouldBe 1
	}
})
