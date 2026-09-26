package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.load
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

private val counterKey = DataPackStateKey<MutableList<Int>>("tests.counter")

class DataPackTests : FunSpec({
	test("state is created once per pack and never shared between packs") {
		val first = dataPack("state_first") {}
		val second = dataPack("state_first") {}

		first.stateOrNull(counterKey) assertsIs null
		first.state(counterKey) { mutableListOf() } += 1
		first.state(counterKey) { mutableListOf(99) } += 2

		first.stateOrNull(counterKey) assertsIs listOf(1, 2)
		second.stateOrNull(counterKey) assertsIs null
	}

	test("generated functions sharing a path with different bodies throw") {
		dataPack("generated_collision") {
			generatedFunction("same") { say("a") }
			generatedFunction("other") { say("a") }.asId() assertsIs "generated_collision:generated_scopes/same"

			shouldThrow<IllegalStateException> { generatedFunction("same") { say("b") } }
		}
	}

	test("unnamed load functions are named after their body") {
		dataPack("unnamed_load") {
			load { say("x") }.asId() assertsIs "unnamed_load:generated_scopes/load_68271a2"
		}
	}
})
