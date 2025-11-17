package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.commands.give
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.advancements.criteria
import io.github.ayfri.kore.features.advancements.triggers.tick
import io.github.ayfri.kore.features.itemmodifiers.functions.setCount
import io.github.ayfri.kore.features.itemmodifiers.itemModifier
import io.github.ayfri.kore.features.recipes.recipes
import io.github.ayfri.kore.features.recipes.types.craftingShaped
import io.github.ayfri.kore.features.recipes.types.key
import io.github.ayfri.kore.features.recipes.types.pattern
import io.github.ayfri.kore.features.recipes.types.result
import io.github.ayfri.kore.features.worldgen.biome.biome
import io.github.ayfri.kore.features.worldgen.dimension.dimension
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.DimensionTypes
import io.github.ayfri.kore.generated.Items
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.readText


fun importingTests() {
	testBasicFunctionExploration()
	testMacroFunctionDetection()
	testCodeGeneration()
	testZipImport()
	testMultipleNamespaces()
	testNestedDirectories()
	testDataDrivenResources()
	testAutoDiscoveryAndWorldgen()
}

inline fun newTest(name: String, block: TestContext.() -> Unit) {
	val context = TestContext(name)
	try {
		context.block()
	} finally {
		// context.delete()
	}
}

fun testBasicFunctionExploration() = newTest("test") {
	val pack = createDataPack("test_datapack") {
		function("simple_function", namespace = "test_namespace") {
			say("Hello, world!")
			give(allPlayers(), Items.DIAMOND)
		}
	}

	pack.generate()

	val explored = explore(pack.path.toString())

	explored.name assertsIs "test_datapack"
	explored.functions.size assertsIs 1
	explored.functions[0].id assertsIs "test_namespace:simple_function"
	explored.functions[0].macroArguments.isEmpty() assertsIs true
}

fun testMacroFunctionDetection() = newTest("macro") {
	val pack = createDataPack("macro_test") {
		function("macro_func", namespace = "mypack") {
			addLine($$"$say Hello $(player_name)!")
			addLine($$"$give @p $(item_id) $(count)")
			say("This is not a macro")
			addLine($$"$tp @s $(x) $(y) $(z)")
		}

		function("normal_func", namespace = "mypack") {
			say("Hello")
			give(allPlayers(), Items.DIAMOND)
		}
	}

	pack.generate()

	val explored = explore(pack.path.toString())

	explored.functions.size assertsIs 2

	val macroFunc = explored.functions.find { it.id == "mypack:macro_func" }
	(macroFunc != null) assertsIs true
	macroFunc!!.macroArguments.size assertsIs 6
	macroFunc.macroArguments.contains("player_name") assertsIs true
	macroFunc.macroArguments.contains("item_id") assertsIs true
	macroFunc.macroArguments.contains("count") assertsIs true
	macroFunc.macroArguments.contains("x") assertsIs true
	macroFunc.macroArguments.contains("y") assertsIs true
	macroFunc.macroArguments.contains("z") assertsIs true

	val normalFunc = explored.functions.find { it.id == "mypack:normal_func" }
	(normalFunc != null) assertsIs true
	normalFunc!!.macroArguments.isEmpty() assertsIs true
}

fun testCodeGeneration() = newTest("codegen") {
	val pack = createDataPack("codegen_test") {
		function("func_one", namespace = "mypack") {
			say("Function one")
		}

		function("func_two", namespace = "mypack") {
			say("Function two")
		}

		function("nested_func", namespace = "mypack", directory = "subfolder") {
			say("Nested function")
		}

		function("with_macro", namespace = "mypack") {
			addLine($$"$say Hello $(name)!")
		}
	}

	pack.generate()

	val explored = explore(pack.path.toString())
	val srcDir = srcDir()

	writeFiles(explored, srcDir)

	val generatedFile = srcDir.resolve("CodegenTest.kt")
	generatedFile.toFile().exists() assertsIs true

	val content = generatedFile.readText()

	content.contains("package kore.dependencies.codegentest") assertsIs true
	content.contains("data object CodegenTest") assertsIs true
	content.contains("const val NAMESPACE: String = \"mypack\"") assertsIs true
	content.contains("const val PATH: String =") assertsIs true

	val hasFuncOne = content.contains("FuncOne") || content.contains("FUNC_ONE")
	val hasFuncTwo = content.contains("FuncTwo") || content.contains("FUNC_TWO")
	val hasWithMacro = content.contains("WithMacro") || content.contains("WITH_MACRO")

	hasFuncOne assertsIs true
	hasFuncTwo assertsIs true
	hasWithMacro assertsIs true

	val hasSubfolder = content.contains("Subfolder")
	val hasNestedFunc = content.contains("NESTED_FUNC") || content.contains("NestedFunc")

	hasSubfolder assertsIs true
	hasNestedFunc assertsIs true

	content.contains(": FunctionArgument") assertsIs true
	content.contains("override fun asId()") assertsIs true
	content.contains("override val namespace: String = NAMESPACE") assertsIs true
}

fun testZipImport() = newTest("zip") {
	val pack = createDataPack("zip_test") {
		function("zipped_func", namespace = "zippack") {
			say("From zip")
		}
	}

	pack.generateZip()

	val zipFile = tempDir.resolve("zip_test.zip")
	val explored = explore(zipFile.toString())

	explored.name assertsIs "zip_test.zip"
	explored.functions.size assertsIs 1
	explored.functions[0].id assertsIs "zippack:zipped_func"
}

fun testMultipleNamespaces() = newTest("multinamespace") {
	val pack = createDataPack("multi_ns") {
		function("func1", namespace = "namespace1") {
			say("NS1")
		}

		function("func2", namespace = "namespace2") {
			say("NS2")
		}

		advancement("adv1") {
			namespace = "namespace1"
			criteria {
				tick("test")
			}
		}

		advancement("adv2") {
			namespace = "namespace2"
			criteria {
				tick("test")
			}
		}
	}

	pack.generate()

	val explored = explore(pack.path.toString())

	explored.functions.size assertsIs 2
	explored.functions.any { it.id == "namespace1:func1" } assertsIs true
	explored.functions.any { it.id == "namespace2:func2" } assertsIs true

	// Test code generation with multiple namespaces
	val srcDir = srcDir()
	writeFiles(explored, srcDir)

	val generatedFile = srcDir.resolve("MultiNs.kt")
	generatedFile.toFile().exists() assertsIs true

	val content = generatedFile.readText()

	// Should have namespace objects
	content.contains("data object Namespace1") assertsIs true
	content.contains("data object Namespace2") assertsIs true

	// Each namespace object should have its own NAMESPACE constant
	val namespace1Section = content.substringAfter("data object Namespace1")
	namespace1Section.contains("const val NAMESPACE: String = \"namespace1\"") assertsIs true

	val namespace2Section = content.substringAfter("data object Namespace2")
	namespace2Section.contains("const val NAMESPACE: String = \"namespace2\"") assertsIs true

	// Each namespace should have its own Functions enum
	namespace1Section.contains("enum class Functions") assertsIs true
	namespace2Section.contains("enum class Functions") assertsIs true

	// Each namespace should have its own Advancements enum
	namespace1Section.contains("enum class Advancements") assertsIs true
	namespace2Section.contains("enum class Advancements") assertsIs true

	// Should NOT have a top-level NAMESPACE constant (only in namespace objects)
	val beforeNamespace1 = content.substringBefore("data object Namespace1")
	beforeNamespace1.contains("const val NAMESPACE") assertsIs false
}

fun testNestedDirectories() = newTest("nested") {
	val pack = createDataPack("nested_test") {
		function("deep_func", namespace = "pack", directory = "level1/level2/level3") {
			say("Deep")
		}
	}

	pack.generate()

	val explored = explore(pack.path.toString())

	explored.functions.size assertsIs 1
	explored.functions[0].id assertsIs "pack:level1/level2/level3/deep_func"
}

fun testDataDrivenResources() = newTest("resources") {
	val pack = createDataPack("resources_test") {
		function("test_func", namespace = "mypack") {
			say("Test function")
		}

		// Create advancements using Kore DSL
		advancement("simple_advancement") {
			namespace = "mypack"
			criteria {
				tick("test")
			}
		}

		advancement("nested_advancement") {
			namespace = "mypack"
			fileName = "nested/nested_advancement"
			criteria {
				tick("test")
			}
		}

		// Create recipes using Kore DSL
		recipes {
			craftingShaped("simple_recipe") {
				pattern(" D ", " S ", "   ")
				key("D", Items.DIAMOND)
				key("S", Items.STICK)
				result(Items.DIAMOND_SWORD)
			}
			// Set namespace on the RecipeFile that was created
			recipes.last().namespace = "mypack"

			craftingShaped("nested_recipe") {
				pattern("DDD", "   ", "   ")
				key("D", Items.DIAMOND)
				result(Items.DIAMOND)
			}
			recipes.last().namespace = "mypack"
			recipes.last().fileName = "subfolder/nested_recipe"
		}

		// Create item modifiers using Kore DSL
		itemModifier("simple_modifier") {
			namespace = "mypack"
			setCount(1f)
		}

		itemModifier("nested_modifier") {
			namespace = "mypack"
			fileName = "deep/nested_modifier"
			setCount(2f)
		}
	}

	pack.generate()

	val explored = explore(pack.path.toString())

	// Verify functions
	explored.functions.size assertsIs 1
	explored.functions[0].id assertsIs "mypack:test_func"

	// Verify advancements
	explored.resources.containsKey("advancement") assertsIs true
	explored.resources["advancement"]!!.size assertsIs 2
	explored.resources["advancement"]!!.any { it.id == "mypack:simple_advancement" } assertsIs true
	explored.resources["advancement"]!!.any { it.id == "mypack:nested/nested_advancement" } assertsIs true

	// Verify recipes
	explored.resources.containsKey("recipe") assertsIs true
	explored.resources["recipe"]!!.size assertsIs 2
	explored.resources["recipe"]!!.any { it.id == "mypack:simple_recipe" } assertsIs true
	explored.resources["recipe"]!!.any { it.id == "mypack:subfolder/nested_recipe" } assertsIs true

	// Verify item_modifiers
	explored.resources.containsKey("item_modifier") assertsIs true
	explored.resources["item_modifier"]!!.size assertsIs 2
	explored.resources["item_modifier"]!!.any { it.id == "mypack:simple_modifier" } assertsIs true
	explored.resources["item_modifier"]!!.any { it.id == "mypack:deep/nested_modifier" } assertsIs true

	// Test code generation
	val srcDir = srcDir()
	writeFiles(explored, srcDir)

	val generatedFile = srcDir.resolve("ResourcesTest.kt")
	generatedFile.toFile().exists() assertsIs true

	val content = generatedFile.readText()

	// Verify it contains sealed interfaces for each resource type (nested resources)
	content.contains("sealed interface Advancements") assertsIs true
	content.contains("sealed interface Recipes") assertsIs true
	content.contains("sealed interface ItemModifiers") assertsIs true

	// Verify it contains the resources (data objects for top-level, enums for nested)
	val hasSimpleAdvancement = content.contains("SimpleAdvancement")
	val hasSimpleRecipe = content.contains("SimpleRecipe")
	val hasSimpleModifier = content.contains("SimpleModifier")

	// Also verify nested resources are present as enums
	val hasNestedAdvancement = content.contains("NESTED_ADVANCEMENT")
	val hasNestedRecipe = content.contains("NESTED_RECIPE")
	val hasNestedModifier = content.contains("NESTED_MODIFIER")

	hasSimpleAdvancement assertsIs true
	hasSimpleRecipe assertsIs true
	hasSimpleModifier assertsIs true
	hasNestedAdvancement assertsIs true
	hasNestedRecipe assertsIs true
	hasNestedModifier assertsIs true
}

fun testAutoDiscoveryAndWorldgen() = newTest("autodiscovery") {
	val pack = createDataPack("autodiscovery_test") {
		// Create some advancements
		advancement("test_adv") {
			namespace = "mypack"
			criteria {
				tick("test")
			}
		}

		biome("custom_biome") {
			namespace = "mypack"
		}

		dimension("custom_dimension", DimensionTypes.OVERWORLD) {
			namespace = "mypack"
		}

		// Create some loot tables (new auto-discovered type)
		// We'll create them by directly accessing the file structure
		function("dummy", namespace = "mypack") {
			say("dummy")
		}
	}

	pack.generate()

	// Manually add loot_table files to test auto-discovery
	// Kore generates into a nested structure: packName/packName/data/...
	// So we need to create our manual file in the same nested location
	val packPath = Path(pack.path.toString())
	val nestedPackPath = packPath.resolve(pack.name) // Go into the nested directory
	val dataDir = nestedPackPath.resolve("data").resolve("mypack").resolve("loot_table")
	Files.createDirectories(dataDir)
	Files.writeString(
		dataDir.resolve("test_loot.json"),
		"""{"type":"minecraft:block","pools":[]}"""
	)

	val explored = explore(pack.path.toString())

	// Verify auto-discovery found advancement
	explored.resources.containsKey("advancement") assertsIs true
	explored.resources["advancement"]!!.any { it.id == "mypack:test_adv" } assertsIs true

	// Verify auto-discovery found dimension
	explored.resources.containsKey("dimension") assertsIs true
	explored.resources["dimension"]!!.any { it.id == "mypack:custom_dimension" } assertsIs true

	// Verify auto-discovery found loot_table
	explored.resources.containsKey("loot_table") assertsIs true
	explored.resources["loot_table"]!!.any { it.id == "mypack:test_loot" } assertsIs true

	// Verify auto-discovery found biome
	explored.resources.containsKey("worldgen/biome") assertsIs true
	explored.resources["worldgen/biome"]!!.any { it.id == "mypack:custom_biome" } assertsIs true

	// Test code generation with auto-discovered types
	val srcDir = srcDir()
	writeFiles(explored, srcDir)

	val generatedFile = srcDir.resolve("AutodiscoveryTest.kt")
	generatedFile.toFile().exists() assertsIs true

	val content = generatedFile.readText()

	// Verify both resource types are generated
	content.contains("Advancements : AdvancementArgument") assertsIs true
	content.contains("Dimensions : DimensionArgument") assertsIs true
	content.contains("LootTables : LootTableArgument") assertsIs true
	content.contains("TEST_LOOT") assertsIs true
	content.contains("data object Worldgen") assertsIs true
	content.contains("Biomes : BiomeArgument") assertsIs true
}

infix fun <T> T.assertsIs(expected: T) {
	if (this != expected) {
		error("Expected '$expected' but got '$this'")
	}
}

infix fun String.assertsIs(expected: String) {
	if (this != expected) {
		error("Expected:\n$expected\n\nBut got:\n$this")
	}
}
