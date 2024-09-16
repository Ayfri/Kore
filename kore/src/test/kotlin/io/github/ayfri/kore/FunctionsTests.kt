package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.setTag
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

	load {
		val generatedFolder = DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER

		var callToGeneratedFunction = datapack.generatedFunction("test") {
			say("Hello, world!")
		}

		function(callToGeneratedFunction) assertsIs "function function_tests:$generatedFolder/test"

		callToGeneratedFunction = datapack.generatedFunction("test", directory = "my_functions") {
			say("Hello, world 2!")
		}

		function(callToGeneratedFunction) assertsIs "function function_tests:$generatedFolder/my_functions/test"

		callToGeneratedFunction = datapack.generatedFunction("test", namespace = "my_namespace") {
			say("Hello, world 3!")
		}

		function(callToGeneratedFunction) assertsIs "function my_namespace:$generatedFolder/test"

		callToGeneratedFunction = datapack.generatedFunction("test", namespace = "my_namespace", directory = "my_functions") {
			say("Hello, world 4!")
		}

		function(callToGeneratedFunction) assertsIs "function my_namespace:$generatedFolder/my_functions/test"

		function("test") {
			comment("This is a comment.")
			addBlankLine()
			addLine("# This is a comment.")
			say("hello")

			commandLines.size assertsIs 1
			isInlinable assertsIs true
		}
	}

	function("inside_tag") {
		setTag("tick", "minecraft")
		setTag("my_tag")
		say("Hello, world!")
	}
}.apply {
	val dpName = "function_tests"
	val data = "$dpName/data"
	val fnName = "test.mcfunction"
	val generatedFolder = DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER
	assertFileGenerated("$data/$dpName/function/$fnName")
	assertFileGenerated("$data/$dpName/function/my_functions/$fnName")
	assertFileGenerated("$data/my_namespace/function/$fnName")
	assertFileGenerated("$data/my_namespace/function/my_functions/$fnName")
	assertFileGenerated("$data/$dpName/function/$generatedFolder/$fnName")
	assertFileGenerated("$data/$dpName/function/$generatedFolder/my_functions/$fnName")
	assertFileGenerated("$data/my_namespace/function/$generatedFolder/$fnName")
	assertFileGenerated("$data/my_namespace/function/$generatedFolder/my_functions/$fnName")
	assertFileGenerated("$data/minecraft/tags/function/load.json")
	assertFileGenerated("$data/minecraft/tags/function/tick.json")
	assertFileGenerated("$data/$dpName/tags/function/my_tag.json")
	generate()
}
