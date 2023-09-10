package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.buildNbtCompound

fun Function.functionTests() {
	val arguments = buildNbtCompound { this["foo"] = 1 }
	val storage = storage("foo")

	function("tests") assertsIs "function ${datapack.name}:tests"
	function("tests", arguments = arguments) assertsIs "function ${datapack.name}:tests {foo:1}"
	function("tests", arguments = storage, path = "test") assertsIs "function ${datapack.name}:tests with storage minecraft:foo test"

	function("unit_tests", "function_tests", true) assertsIs "function #unit_tests:function_tests"
	function("unit_tests", "function_tests", true, arguments) assertsIs "function #unit_tests:function_tests {foo:1}"
	function(
		"unit_tests",
		"function_tests",
		true,
		storage,
		"test"
	) assertsIs "function #unit_tests:function_tests with storage minecraft:foo test"

	val otherFunction = datapack.function("other_function") { say("other function") }

	function(otherFunction) assertsIs "function ${otherFunction.asId()}"
	function(otherFunction, arguments = arguments) assertsIs "function ${otherFunction.asId()} {foo:1}"
	function(otherFunction, arguments = storage, path = "test") assertsIs "function ${otherFunction.asId()} with storage minecraft:foo test"
}
