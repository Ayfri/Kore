package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.features.tags.functionTag
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

	function("multiline_test") {
		addLine("say hello\nsay world")
		lines.size assertsIs 2
		lines[0] assertsIs "say hello"
		lines[1] assertsIs "say world"
	}

	function("multiline_three_lines") {
		addLine(
			"""
			say one
			say two
			say three
		""".trimIndent()
		)
		lines.size assertsIs 3
		lines[0] assertsIs "say one"
		lines[1] assertsIs "say two"
		lines[2] assertsIs "say three"
	}

	function("single_line_no_newline") {
		addLine("say hello")
		lines.size assertsIs 1
		lines[0] assertsIs "say hello"
	}

	function("empty_line_split") {
		addLine("say hello\n\nsay world")
		lines.size assertsIs 3
		lines[0] assertsIs "say hello"
		lines[1] assertsIs ""
		lines[2] assertsIs "say world"
	}

	function("mixed_addline_and_commands") {
		say("first")
		addLine("say second\nsay third")
		say("fourth")
		lines.size assertsIs 4
		commandLines.size assertsIs 4
	}

	function("multiline_tostring") {
		addLine("say hello\nsay world")
		toString() assertsIs "say hello\nsay world"
	}

	function("inside_tag") {
		setTag("tick", "minecraft")
		setTag("my_tag")
		say("Hello, world!")
	}

	val customTag = functionTag("custom_tag", "custom_namespace")

	function("inside_tag_with_argument") {
		setTag(customTag)
		say("Hello from tag argument!")
	}

	function("inside_tag_custom_entry_namespace") {
		setTag("my_custom_tag", entryNamespace = "other_namespace")
		say("Hello with custom entry namespace!")
	}

	function("inside_tag_with_required") {
		setTag("required_tag", entryIsRequired = true)
		say("Hello with required entry!")
	}

	function("inside_tag_is_tag_entry") {
		setTag("tag_entry_tag", entryIsTag = true)
		say("Hello as tag entry!")
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
	assertFileGenerated("$data/custom_namespace/tags/function/custom_tag.json")
	assertFileGenerated("$data/$dpName/tags/function/my_custom_tag.json")
	assertFileGenerated("$data/$dpName/tags/function/required_tag.json")
	assertFileGenerated("$data/$dpName/tags/function/tag_entry_tag.json")
	generate()
}
