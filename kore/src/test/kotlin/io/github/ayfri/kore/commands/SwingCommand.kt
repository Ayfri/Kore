package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.kotest.core.spec.style.FunSpec

fun Function.swingTests() {
	swing(self(), SwingHand.MAINHAND) assertsIs "swing @s mainhand"
	swing(self(), SwingHand.OFFHAND) assertsIs "swing @s offhand"
	swing(allEntities(), SwingHand.MAINHAND) assertsIs "swing @e mainhand"
}

class SwingCommandTests : FunSpec({
	test("swing") {
		dataPack("unit_tests") {
			load { swingTests() }
		}.generate()
	}
})
