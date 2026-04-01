package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.kotest.core.spec.style.FunSpec

fun Function.rideTests() {
	ride(allEntities(true)) {
		mount(self()) assertsIs "ride @e[limit=1] mount @s"
		dismount() assertsIs "ride @e[limit=1] dismount"
	}
}

class RideCommandTests : FunSpec({
	test("ride") {
		dataPack("unit_tests") {
			load { rideTests() }
		}.generate()
	}
})
