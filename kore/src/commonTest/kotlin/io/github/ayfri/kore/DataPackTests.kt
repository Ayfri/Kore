package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.generatedFunctionName
import io.github.ayfri.kore.functions.hashedGeneratedFunction
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.utils.KoreLogger
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs

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

	test("hashed generated functions are named after their body and shared by identical bodies") {
		dataPack("hashed") {
			val first = hashedGeneratedFunction("on_click") { say("x") }
			first.asId() assertsIs "hashed:generated_scopes/${generatedFunctionName("on_click", listOf("say x"))}"
			hashedGeneratedFunction("on_click") { say("x") } shouldBeSameInstanceAs first
			generatedFunctions.size assertsIs 1
		}
	}

	test("a generated function mutated after registration is still found by path") {
		dataPack("mutated") {
			val function = generatedFunction("grow") {}
			(function as Function).lines += "say x"

			generatedFunction("grow") { say("x") } shouldBeSameInstanceAs function
			shouldThrow<IllegalStateException> { generatedFunction("grow") { say("y") } }
		}
	}

	test("two files with one path and different contents throw, identical ones are written once") {
		dataPack("duplicates") {
			function("same") { say("a") }
			function("same") { say("a") }
			exportAsStrings()["data/duplicates/function/same.mcfunction"] assertsIs "say a"

			function("same") { say("b") }
			shouldThrow<IllegalStateException> { exportAsStrings() }
		}
	}

	test("debug mode still on when written wraps the file in start and finish markers") {
		dataPack("debug_markers") {
			function("traced") {
				startDebug()
				say("x")
			}

			fun marker(prefix: String) =
				"""tellraw @a [{"type":"text","text":""},{"type":"text","color":"gray","italic":true,"text":"$prefix"},{"type":"text","bold":true,"click_event":{"action":"run_command","command":"function debug_markers:traced"},"color":"white","hover_event":{"action":"show_text","value":{"type":"text","color":"gray","italic":true,"text":"Click to execute function"}},"italic":true,"text":"debug_markers:traced"}]"""

			val lines = exportAsStrings().getValue("data/debug_markers/function/traced.mcfunction").lines()
			lines.first() assertsIs marker("Running function ")
			lines[1] assertsIs "say x"
			lines.last() assertsIs marker("Finished running function ")
			functions.single().lines.first() assertsIs "say x"
		}
	}

	test("the logger filters by level and routes to its handler") {
		val received = mutableListOf<String>()
		val previousHandler = KoreLogger.handler
		val previousLevel = KoreLogger.level
		try {
			KoreLogger.handler = { level, message -> received += "$level $message" }
			KoreLogger.info("a")
			KoreLogger.level = KoreLogger.Level.WARN
			KoreLogger.info("b")
			KoreLogger.warn("c")
			KoreLogger.level = KoreLogger.Level.OFF
			KoreLogger.warn("d")
		} finally {
			KoreLogger.handler = previousHandler
			KoreLogger.level = previousLevel
		}

		received assertsIs listOf("INFO a", "WARN c")
	}
})
