package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.load
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class DataPackTests : FunSpec({
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
