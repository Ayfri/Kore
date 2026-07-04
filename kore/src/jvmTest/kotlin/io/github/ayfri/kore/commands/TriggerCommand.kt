package io.github.ayfri.kore.commands

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.kotest.core.spec.style.FunSpec

fun Function.triggerTests() {
	trigger("test") {
		add(1) assertsIs "trigger test add 1"
		set(1) assertsIs "trigger test set 1"
		remove(1) assertsIs "trigger test add -1"
	}
}

class TriggerCommandTests : FunSpec({
	test("trigger") {
		dataPack("unit_tests") {
			load { triggerTests() }
		}.generate()
	}
})
