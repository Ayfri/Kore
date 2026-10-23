package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.enums.SwingAnimationType
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.kotest.core.spec.style.FunSpec

fun Function.swingTests() {
	swing() assertsIs "swing"
	swing(allEntities()) assertsIs "swing @e"
	swing(self(), SwingHand.MAINHAND) assertsIs "swing @s mainhand"
	swing(self(), SwingHand.OFFHAND) assertsIs "swing @s offhand"
	swing(allEntities(), SwingHand.MAINHAND) assertsIs "swing @e mainhand"
	swing(self(), SwingHand.MAINHAND, SwingAnimationType.STAB, 10.ticks) assertsIs "swing @s mainhand stab 10"
	swing(self(), SwingHand.OFFHAND, SwingAnimationType.WHACK) assertsIs "swing @s offhand whack"
	swing(self(), SwingHand.MAINHAND, SwingAnimationType.NONE) assertsIs "swing @s mainhand none"
	swing(allEntities(), SwingHand.MAINHAND, SwingAnimationType.WHACK, 2.seconds) assertsIs "swing @e mainhand whack 2s"
}

class SwingCommandTests : FunSpec({
	test("swing") {
		dataPack("unit_tests") {
			load { swingTests() }
		}
	}
})
