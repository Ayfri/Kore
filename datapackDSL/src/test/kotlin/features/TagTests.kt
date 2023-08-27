package features

import DataPack
import commands.function
import commands.say
import features.tags.functionTag
import features.tags.tag
import functions.function
import functions.load
import utils.assertsIs

fun DataPack.tagTests() {
	val myTag = functionTag(fileName = "shiny_functions") {
		this += "my_function"
		this += function("my_function_2") {
			say("Hello, world!")
		}
	}

	tags.last() assertsIs """
		{
			"replace": false,
			"values": [
				"my_function",
				"features_tests:my_function_2"
			]
		}
	""".trimIndent()

	tag(fileName = "untyped_tag", type = "tests", replace = true) {
		this += "minecraft:test" to true
	}

	tags.last() assertsIs """
		{
			"replace": true,
			"values": [
				{
					"id": "minecraft:test",
					"required": true
				}
			]
		}
	""".trimIndent()

	load {
		function(myTag) assertsIs "function #features_tests:shiny_functions"
	}
}
