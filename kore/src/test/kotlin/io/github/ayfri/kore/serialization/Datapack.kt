package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.assertions.*
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.configuration
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.features.recipes.recipes
import io.github.ayfri.kore.features.recipes.types.craftingShapeless
import io.github.ayfri.kore.features.recipes.types.ingredient
import io.github.ayfri.kore.features.recipes.types.result
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.DataPacks
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generation.fabric.author
import io.github.ayfri.kore.generation.fabric.contact
import io.github.ayfri.kore.generation.fabric.fabric
import io.github.ayfri.kore.generation.forge.dependency
import io.github.ayfri.kore.generation.forge.forge
import io.github.ayfri.kore.generation.forge.mod
import io.github.ayfri.kore.generation.mergeWithPacks
import io.github.ayfri.kore.generation.neoforge.NeoForgeDependencyType
import io.github.ayfri.kore.generation.neoforge.dependency
import io.github.ayfri.kore.generation.neoforge.mod
import io.github.ayfri.kore.generation.neoforge.neoForge
import io.github.ayfri.kore.generation.quilt.contact
import io.github.ayfri.kore.generation.quilt.contributor
import io.github.ayfri.kore.generation.quilt.metadata
import io.github.ayfri.kore.generation.quilt.quilt
import io.github.ayfri.kore.pack.features
import io.github.ayfri.kore.pack.filter
import io.github.ayfri.kore.pack.pack
import io.github.ayfri.kore.pack.packFormat
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import kotlinx.io.files.Path

fun datapackTests() {
	jarTests()
	mergeDatapacksTests()
	packMCMetaTests()
	zipTests()
}

fun generatePack(name: String, init: DataPack.() -> Unit = {}) = testDataPack(name) {
	function("test") {
		say("Hello, world!")
	}

	init()
}

fun mergeDatapacksTests() {
	var dp1 = generatePack("dp1") {
		function("test space") {
			say("Hello, world!")
		}
	}
	var dp2 = generatePack("dp2")

	dp1.assertFileGenerated("dp1/data/dp1/function/test.mcfunction")
	dp1.assertFileGenerated("dp1/data/dp1/function/test space.mcfunction")
	dp1.assertFileGenerated("dp1/data/dp2/function/test.mcfunction")
	dp1.generate {
		mergeWithPacks(dp2.dp)
	}

	dp1 = generatePack("dp1") {
		function("test space") {
			say("Hello, world!")
		}
	}
	dp1.assertFileGeneratedInZip("data/dp1/function/test.mcfunction")
	dp1.assertFileGeneratedInZip("data/dp1/function/test space.mcfunction")
	dp1.assertFileGeneratedInZip("data/dp2/function/test.mcfunction")
	dp1.generateZip {
		dp2.dp.generated = false
		mergeWithPacks(dp2.dp)
	}

	val dp3 = generatePack("dp3") {
		pack {
			minFormat = packFormat(10)
			maxFormat = packFormat(10)
			packFormat = packFormat(10)
		}
	}
	val dp4 = generatePack("dp4") {
		pack {
			minFormat = packFormat(11)
			maxFormat = packFormat(11)
			packFormat = packFormat(11)
		}
	}

	dp3.generate {
		// Should print warning
		mergeWithPacks(dp4.dp)
	}

	val dp5 = generatePack("dp5") {
		pack {
			minFormat = packFormat(10)
			maxFormat = packFormat(12)
			packFormat = packFormat(10)
		}
	}

	val dp6 = generatePack("dp6") {
		pack {
			minFormat = packFormat(11)
			maxFormat = packFormat(13)
			packFormat = packFormat(11)
		}
	}

	dp5.generate {
		// Should not print warning as supported formats are compatible.
		mergeWithPacks(dp6.dp)
	}

	val dp7 = generatePack("dp7") {
		pack {
			minFormat = packFormat(10)
			maxFormat = packFormat(11)
			packFormat = packFormat(10)
		}
	}

	val dp8 = generatePack("dp8") {
		pack {
			minFormat = packFormat(12)
			maxFormat = packFormat(13)
			packFormat = packFormat(12)
		}
	}

	dp7.generate {
		// Should print warning as formats and supported formats are not compatible.
		mergeWithPacks(dp8.dp)
	}

	val dp9 = generatePack("dp9") {
		recipes {
			craftingShapeless("craft") {
				ingredient(Items.STONE)
				result(Items.DIAMOND)
			}
		}

		load("my_load_1") {
			say("Hello, load!")
		}

		configuration {
			prettyPrint = true
			prettyPrintIndent = "\t"
		}
	}

	val dp10 = generatePack("dp10") {
		predicate() {
			namespace = "minecraft"

			randomChance(0.5f)
		}

		load("my_load_2") {
			say("Hello, load!")
		}
	}

	dp9.assertFileGenerated("dp9/data/dp9/recipe/craft.json")
	dp9.assertFileGenerated("dp9/data/minecraft/predicate/predicate.json")
	dp9.assertFileGenerated("dp9/data/minecraft/tags/function/load.json")
	dp9.assertFileJsonContent(
		"dp9/data/minecraft/tags/function/load.json", """
		{
			"replace": false,
			"values": [
				"dp9:${DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER}/my_load_1",
				"dp10:${DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER}/my_load_2"
			]
		}
	""".trimIndent()
	)

	dp9.generate {
		mergeWithPacks(dp10.dp)
	}
}

fun jarTests() {
	val dp1 = generatePack("jar_tests")

	dp1.generateJar {
		fabric {
			version = "1.2.5"
			contact {
				email = "kore@kore.kore"
				homepage = "https://kore.ayfri.com"
			}

			author("Ayfri")
		}

		forge {
			mod {
				authors = "Ayfri"
				credits = "Generated by Kore"

				dependency("my_dependency") {
					mandatory = true
					version = "1.2.5"
				}
			}
		}

		quilt("kore") {
			metadata {
				contact {
					email = "kore@kore.kore"
					homepage = "https://kore.ayfri.com"
				}
				contributor("Ayfri", "Author")
			}

			version = "1.2.5"
		}

		neoForge {
			mod {
				authors = "Ayfri"
				credits = "Generated by Kore"

				dependency("my_dependency") {
					type = NeoForgeDependencyType.REQUIRED
					version = "1.2.5"
				}
			}
		}

		dp1.assertFileGeneratedInJar("fabric.mod.json")
		dp1.assertFileGeneratedInJar("META-INF/mods.toml")
		dp1.assertFileGeneratedInJar("quilt.mod.json")
		dp1.assertFileGeneratedInJar("META-INF/neoforge.mods.toml")
	}
}

fun packMCMetaTests() = testDataPack("test") {
	pretty()

	pack {
		minFormat = packFormat(16)
		maxFormat = packFormat(18)
		packFormat = packFormat(16)
		description = textComponent("Test pack", color = Color.AQUA)
	}

	filter {
		block(path = "stone*")
	}

	features(DataPacks.REDSTONE_EXPERIMENTS)

	iconPath = Path("kore", "src", "test", "resources", "kore_icon.png")
}.dp.generatePackMCMetaFile() assertsIsJson """
	{
		"pack": {
			"description": {
				"text": "Test pack",
				"color": "aqua",
				"type": "text"
			},
			"min_format": 16,
			"max_format": 18,
			"pack_format": 16,
			"supported_formats": {
				"min_inclusive": 16,
				"max_inclusive": 18
			}
		},
		"features": {
			"enabled": [
				"redstone_experiments"
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

	function("my_function") {
		say("Hello, world!")
	}

	function("my_function_2", directory = "subfolder") {
		say("Hello, world 2!")
	}

	load {
		say("Hello, load!")
	}

	tick(directory = "subfolder") {
		say("Hello, tick!")
	}
}.apply {
	assertFileGenerated("zip_tests.zip")

	val myFunction = dp.functions[0]
	assertFileGeneratedInZip("data/${myFunction.namespace}/function/${myFunction.name}.mcfunction")

	val myFunction2 = dp.functions[1]
	assertFileGeneratedInZip("data/${myFunction2.namespace}/function/${myFunction2.directory}/${myFunction2.name}.mcfunction")

	val loadFunction = dp.generatedFunctions[0]
	assertFileGeneratedInZip("data/${loadFunction.namespace}/function/${dp.configuration.generatedFunctionsFolder}/${loadFunction.name}.mcfunction")

	val tickFunction = dp.generatedFunctions[1]
	val tickDirectory = tickFunction.directory.removePrefix(dp.configuration.generatedFunctionsFolder + "/")
	assertFileGeneratedInZip("data/${tickFunction.namespace}/function/${dp.configuration.generatedFunctionsFolder}/${tickDirectory}/${tickFunction.name}.mcfunction")
	generateZip()
}
