package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.features.tags.functionTag
import io.github.ayfri.kore.features.tags.tag
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

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

class TagTests : FunSpec({
	test("tag") {
		testDataPack("features_tests") {
			pretty()
			tagTests()
		}.apply {
			assertGeneratorsGenerated()
			generate()
		}
	}
})
