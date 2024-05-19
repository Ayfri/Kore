package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.assertions.assertFileGeneratedInZip
import io.github.ayfri.kore.assertions.assertsIsJson
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.DataPacks
import io.github.ayfri.kore.pack.features
import io.github.ayfri.kore.pack.filter
import io.github.ayfri.kore.pack.pack
import io.github.ayfri.kore.pack.supportedFormat
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import kotlin.io.path.Path

fun datapackTests() {
	packMCMetaTests()
	zipTests()
}

fun packMCMetaTests() = testDataPack("test") {
	pretty()

	pack {
		format = 16
		description = textComponent("Test pack", color = Color.AQUA)
		supportedFormat(16..18)
	}

	filter {
		block(path = "stone*")
	}

	features(DataPacks.BUNDLE)

	iconPath = Path("kore", "src", "test", "resources", "kore_icon.png")
}.apply {
	assertFileGenerated("pack.mcmeta")
	assertFileGenerated("pack.png")
}.dp.generatePackMCMetaFile() assertsIsJson """
	{
		"pack": {
			"pack_format": 16,
			"description": {
				"text": "Test pack",
				"color": "aqua",
				"type": "text"
			},
			"supported_formats": {
				"min_inclusive": 16,
				"max_inclusive": 18
			}
		},
		"features": {
			"enabled": [
				"bundle"
			]
		},
		"filter": {
			"block": [
				{
					"path": "stone*"
				}
			]
		}
	}
""".trimIndent()

fun zipTests() = testDataPack("zip_tests") {
	pretty()

	function("my function") {
		say("Hello, world!")
	}

	function("my function 2", directory = "subfolder") {
		say("Hello, world 2!")
	}

	load {
		say("Hello, load!")
	}

	tick(directory = "subfolder") {
		say("Hello, tick!")
	}
}.apply {
	assertFileGenerated("test.zip")

	val myFunction = dp.functions[0]
	assertFileGeneratedInZip("data/${myFunction.namespace}/functions/${myFunction.name}.mcfunction")

	val myFunction2 = dp.functions[1]
	assertFileGeneratedInZip("data/${myFunction2.namespace}/functions/${myFunction2.directory}/${myFunction2.name}.mcfunction")

	val loadFunction = dp.generatedFunctions[0]
	assertFileGeneratedInZip("data/${loadFunction.namespace}/functions/${dp.configuration.generatedFunctionsFolder}/${loadFunction.name}.mcfunction")

	val tickFunction = dp.generatedFunctions[1]
	val tickDirectory = tickFunction.directory.removePrefix(dp.configuration.generatedFunctionsFolder + "/")
	assertFileGeneratedInZip("data/${tickFunction.namespace}/functions/${dp.configuration.generatedFunctionsFolder}/${tickDirectory}/${tickFunction.name}.mcfunction")
	generateZip()
}
