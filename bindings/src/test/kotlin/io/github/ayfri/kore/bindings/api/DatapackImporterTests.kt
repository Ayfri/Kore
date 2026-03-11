package io.github.ayfri.kore.bindings.api

import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.commands.give
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.advancements.criteria
import io.github.ayfri.kore.features.advancements.triggers.tick
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.bindings.assertsIs
import io.github.ayfri.kore.bindings.newTest
import io.github.ayfri.kore.bindings.api.importDatapacks
import io.github.ayfri.kore.bindings.api.exploreDatapacks
import kotlin.io.path.readText

fun apiTests() {
	testBasicImport()
	testExploreOnly()
	testCustomPackageName()
	testCustomRemapName()
	testMultipleDatapacks()
	testGlobalConfiguration()
	testMixedConfiguration()
	testMultipleNamespacesInSinglePack()
}

fun testBasicImport() = newTest("basic_import") {
	val pack = createDataPack("basic_test") {
		function("test_func", namespace = "mypack") {
			say("Hello from DSL!")
			give(allPlayers(), Items.DIAMOND)
		}
	}

	pack.generate()

	val datapacks = importDatapacks {
		configuration {
			outputPath(srcDir())
		}

		url(pack.path.toString())
	}

	datapacks.size assertsIs 1
	datapacks[0].name assertsIs "basic_test"
	datapacks[0].functions.size assertsIs 1
	datapacks[0].functions[0].id assertsIs "mypack:test_func"

	val generatedFile = srcDir().resolve("BasicTest.kt")
	generatedFile.toFile().exists() assertsIs true
}

fun testExploreOnly() = newTest("explore_only") {
	val pack = createDataPack("explore_test") {
		function("func1", namespace = "testpack") {
			say("Function 1")
		}
		function("func2", namespace = "testpack") {
			say("Function 2")
		}
	}

	pack.generate()

	// Only explore, don't write
	val datapacks = exploreDatapacks {
		url(pack.path.toString())
	}

	datapacks.size assertsIs 1
	datapacks[0].functions.size assertsIs 2
	datapacks[0].functions.any { it.id == "testpack:func1" } assertsIs true
	datapacks[0].functions.any { it.id == "testpack:func2" } assertsIs true

	// Verify no files were generated
	srcDir().toFile().listFiles()?.isEmpty() assertsIs true
}

fun testCustomPackageName() = newTest("custom_package") {
	val pack = createDataPack("custom_pack") {
		function("my_func", namespace = "custom") {
			say("Custom package test")
		}
	}

	pack.generate()

	importDatapacks {
		configuration {
			outputPath(srcDir())
		}

		url(pack.path.toString()) {
			packageName = "com.example.custom"
		}
	}

	val generatedFile = srcDir().resolve("CustomPack.kt")
	generatedFile.toFile().exists() assertsIs true

	val content = generatedFile.readText()
	content.contains("package com.example.custom") assertsIs true
}

fun testCustomRemapName() = newTest("custom_remap") {
	val pack = createDataPack("remap_test") {
		function("test", namespace = "pack") {
			say("Test")
		}
	}

	pack.generate()

	importDatapacks {
		configuration {
			outputPath(srcDir())
		}

		url(pack.path.toString()) {
			remappings { objectName("MyCustomName") }
		}
	}

	val generatedFile = srcDir().resolve("MyCustomName.kt")
	generatedFile.toFile().exists() assertsIs true

	val content = generatedFile.readText()
	content.contains("data object MyCustomName") assertsIs true
}

fun testMultipleDatapacks() = newTest("multiple_packs") {
	val pack1 = createDataPack("pack1") {
		function("func1", namespace = "p1") {
			say("Pack 1")
		}
	}

	val pack2 = createDataPack("pack2") {
		function("func2", namespace = "p2") {
			say("Pack 2")
		}
	}

	pack1.generate()
	pack2.generate()

	val datapacks = importDatapacks {
		configuration {
			outputPath(srcDir())
		}

		url(pack1.path.toString())
		url(pack2.path.toString())
	}

	datapacks.size assertsIs 2

	val file1 = srcDir().resolve("Pack1.kt")
	val file2 = srcDir().resolve("Pack2.kt")

	file1.toFile().exists() assertsIs true
	file2.toFile().exists() assertsIs true
}

fun testGlobalConfiguration() = newTest("global_config") {
	val pack = createDataPack("global_test") {
		function("func", namespace = "pack") {
			say("Test")
		}
	}

	pack.generate()

	importDatapacks {
		configuration {
			outputPath(srcDir())
			packagePrefix = "com.global.test"
		}

		url(pack.path.toString())
	}

	val generatedFile = srcDir().resolve("GlobalTest.kt")
	generatedFile.toFile().exists() assertsIs true

	val content = generatedFile.readText()
	content.contains("package com.global.test.globaltest") assertsIs true
}

fun testMixedConfiguration() = newTest("mixed_config") {
	val pack1 = createDataPack("mixed1") {
		function("func1", namespace = "p1") {
			say("Pack 1")
		}
	}

	val pack2 = createDataPack("mixed2") {
		function("func2", namespace = "p2") {
			say("Pack 2")
		}

		advancement("test_adv") {
			namespace = "p2"
			criteria {
				tick("test")
			}
		}
	}

	pack1.generate()
	pack2.generate()

	val datapacks = importDatapacks {
		configuration {
			outputPath(srcDir())
			packagePrefix = "com.mixed"
		}

		url(pack1.path.toString()) {
			remappings { objectName("FirstPack") }
		}

		url(pack2.path.toString()) {
			packageName = "com.custom.second"
		}
	}

	datapacks.size assertsIs 2
	datapacks[1].resources.containsKey("advancement") assertsIs true

	val file1 = srcDir().resolve("FirstPack.kt")
	val file2 = srcDir().resolve("Mixed2.kt")

	file1.toFile().exists() assertsIs true
	file2.toFile().exists() assertsIs true

	val content1 = file1.readText()
	val content2 = file2.readText()

	content1.contains("data object FirstPack") assertsIs true
	content1.contains("package com.mixed.mixed1") assertsIs true
	content2.contains("package com.custom.second") assertsIs true
}

fun testMultipleNamespacesInSinglePack() = newTest("multi_ns_single") {
	val pack = createDataPack("multi_namespace_pack") {
		function("func1", namespace = "ns1") {
			say("Namespace 1")
		}

		function("func2", namespace = "ns2") {
			say("Namespace 2")
		}

		advancement("adv1") {
			namespace = "ns1"
			criteria {
				tick("test")
			}
		}

		advancement("adv2") {
			namespace = "ns2"
			criteria {
				tick("test")
			}
		}
	}

	pack.generate()

	val datapacks = importDatapacks {
		configuration {
			outputPath(srcDir())
		}

		url(pack.path.toString())
	}

	datapacks.size assertsIs 1
	datapacks[0].functions.size assertsIs 2

	val generatedFile = srcDir().resolve("MultiNamespacePack.kt")
	generatedFile.toFile().exists() assertsIs true

	val content = generatedFile.readText()

	// Should have namespace objects
	content.contains("data object Ns1") assertsIs true
	content.contains("data object Ns2") assertsIs true

	// Each namespace should have NAMESPACE constant
	val ns1Section = content.substringAfter("data object Ns1")
	ns1Section.contains("const val NAMESPACE: String = \"ns1\"") assertsIs true

	val ns2Section = content.substringAfter("data object Ns2")
	ns2Section.contains("const val NAMESPACE: String = \"ns2\"") assertsIs true

	// Should NOT have a global NAMESPACE at the top level
	val mainObject = content.substringBefore("data object Ns1")
	mainObject.contains("const val NAMESPACE: String") assertsIs false
}
