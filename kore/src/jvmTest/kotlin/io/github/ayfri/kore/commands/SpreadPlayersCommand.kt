package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec2
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.kotest.core.spec.style.FunSpec

fun Function.spreadPlayersTests() {
	spreadPlayers(vec2(), 0.0, 0.0, false, allEntities()) assertsIs "spreadplayers ~ ~ 0 0 false @e"
	spreadPlayers(vec2(), 0.0, 0.0, 0, true, allEntities()) assertsIs "spreadplayers ~ ~ 0 0 under 0 true @e"
}

class SpreadPlayersCommandTests : FunSpec({
	test("spread players") {
		dataPack("unit_tests") {
			load { spreadPlayersTests() }
		}.generate()
	}
})
