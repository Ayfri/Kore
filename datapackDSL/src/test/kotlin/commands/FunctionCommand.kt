package commands

import arguments.types.resources.storage
import functions.Function
import functions.function
import net.benwoodworth.knbt.buildNbtCompound
import utils.assertsIs
import utils.set

fun Function.functionTests() {
	val arguments = buildNbtCompound { this["foo"] = 1 }
	val storage = storage("foo")

	function("tests") assertsIs "function ${datapack.name}:tests"
	function("tests", arguments = arguments) assertsIs "function ${datapack.name}:tests {foo:1}"
	function("tests", arguments = storage) assertsIs "function ${datapack.name}:tests with storage minecraft:foo"

	function("unit_tests", "function_tests", true) assertsIs "function #unit_tests:function_tests"
	function("unit_tests", "function_tests", true, arguments) assertsIs "function #unit_tests:function_tests {foo:1}"
	function("unit_tests", "function_tests", true, storage) assertsIs "function #unit_tests:function_tests with storage minecraft:foo"

	val otherFunction = datapack.function("other_function") { say("other function") }

	function(otherFunction) assertsIs "function ${otherFunction.asId()}"
	function(otherFunction, arguments = arguments) assertsIs "function ${otherFunction.asId()} {foo:1}"
	function(otherFunction, arguments = storage) assertsIs "function ${otherFunction.asId()} with storage minecraft:foo"
}
