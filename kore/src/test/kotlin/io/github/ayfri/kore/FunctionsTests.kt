package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.utils.testDataPack

fun functionsTests() = testDataPack("function_tests") {
	function("test") {
		say("Hello, world!")
	}

	function("test", directory = "my_functions") {
		say("Hello, world!")
	}

	function("test", namespace = "my_namespace") {
		say("Hello, world!")
	}

	function("test", namespace = "my_namespace", directory = "my_functions") {
		say("Hello, world!")
	}

	generatedFunction("test") {
		say("Hello, world!")
	}

	generatedFunction("test", directory = "my_functions") {
		say("Hello, world 2!")
	}

	generatedFunction("test", namespace = "my_namespace") {
		say("Hello, world 3!")
	}

	generatedFunction("test", namespace = "my_namespace", directory = "my_functions") {
		say("Hello, world 4!")
	}
}.apply {
	val dpName = "function_tests"
	val data = "$dpName/data"
	val fnName = "test.mcfunction"
	val generatedFolder = DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER
	assertFileGenerated("$data/$dpName/functions/$fnName")
	assertFileGenerated("$data/$dpName/functions/my_functions/$fnName")
	assertFileGenerated("$data/my_namespace/functions/$fnName")
	assertFileGenerated("$data/my_namespace/functions/my_functions/$fnName")
	assertFileGenerated("$data/$dpName/functions/$generatedFolder/$fnName")
	assertFileGenerated("$data/$dpName/functions/$generatedFolder/my_functions/$fnName")
	assertFileGenerated("$data/my_namespace/functions/$generatedFolder/$fnName")
	assertFileGenerated("$data/my_namespace/functions/$generatedFolder/my_functions/$fnName")
	generate()
}
