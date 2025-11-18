package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.features.tags.biomeTag
import io.github.ayfri.kore.features.tags.blockTag
import io.github.ayfri.kore.features.tags.enchantmentTag
import io.github.ayfri.kore.features.tags.itemTag
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Items
import kotlin.io.path.readText

fun tagsTests() {
	testSimpleBlockTags()
	testNestedEnchantmentTags()
	testMultipleTagTypes()
	testWorldgenBiomeTags()
	testTagCodeGeneration()
}

fun testSimpleBlockTags() = newTest("simple_block_tags") {
	val pack = createDataPack("simple_tags_test") {
		blockTag("my_blocks", namespace = "mypack") {
			this += "#minecraft:logs"
			this += Blocks.DIAMOND_BLOCK
		}
	}

	pack.generate()
	val explored = explore(pack.path.toString())

	// Verify tags are discovered
	explored.resources["tags/block"]?.size assertsIs 1
	explored.resources["tags/block"]?.get(0)?.id assertsIs "mypack:my_blocks"
}

fun testNestedEnchantmentTags() = newTest("nested_enchantment_tags") {
	val pack = createDataPack("nested_tags_test") {
		// Create nested tag structure: tags/enchantment/exclusive_set/custom.json
		enchantmentTag("exclusive_set/custom", namespace = "mypack") {
			this += "minecraft:sharpness"
			this += "minecraft:smite"
		}

		enchantmentTag("powerful", namespace = "mypack") {
			this += "minecraft:sharpness"
		}
	}

	pack.generate()
	val explored = explore(pack.path.toString())

	// Verify nested tags
	explored.resources["tags/enchantment"]?.size assertsIs 2
	val tags = explored.resources["tags/enchantment"]!!.map { it.id }.sorted()
	tags[0] assertsIs "mypack:exclusive_set/custom"
	tags[1] assertsIs "mypack:powerful"
}

fun testMultipleTagTypes() = newTest("multiple_tag_types") {
	val pack = createDataPack("multi_type_tags") {
		blockTag("custom_blocks", namespace = "testpack") {
			this += Blocks.STONE
		}

		itemTag("custom_items", namespace = "testpack") {
			this += Items.DIAMOND
		}
	}

	pack.generate()
	val explored = explore(pack.path.toString())

	// Verify multiple tag types
	explored.resources["tags/block"]?.size assertsIs 1
	explored.resources["tags/item"]?.size assertsIs 1
	explored.resources["tags/block"]?.get(0)?.id assertsIs "testpack:custom_blocks"
	explored.resources["tags/item"]?.get(0)?.id assertsIs "testpack:custom_items"
}

fun testWorldgenBiomeTags() = newTest("worldgen_biome_tags") {
	val pack = createDataPack("worldgen_tags") {
		// Biome tags are created in tags/biome/ directory (not tags/worldgen/biome/)
		biomeTag("custom_biomes", namespace = "mypack") {
			this += "minecraft:plains"
			this += "minecraft:forest"
		}
	}

	pack.generate()
	val explored = explore(pack.path.toString())

	// Verify worldgen tags
	explored.resources["tags/worldgen/biome"]?.size assertsIs 1
	explored.resources["tags/worldgen/biome"]?.get(0)?.id assertsIs "mypack:custom_biomes"
}

fun testTagCodeGeneration() = newTest("tag_codegen") {
	val pack = createDataPack("tag_codegen_test") {
		// Create various tags for code generation testing
		blockTag("custom_blocks", namespace = "tagpack") {
			this += Blocks.STONE
		}

		blockTag("mineable/custom_pickaxe", namespace = "tagpack") {
			this += Blocks.DIAMOND_ORE
		}

		itemTag("custom_items", namespace = "tagpack") {
			this += Items.DIAMOND
		}

		enchantmentTag("powerful", namespace = "tagpack") {
			this += "minecraft:sharpness"
		}
	}

	pack.generate()
	val explored = explore(pack.path.toString())

	// Test code generation
	val srcDir = srcDir()
	writeFiles(explored, srcDir)

	val generatedFile = srcDir.resolve("TagCodegenTest.kt")
	generatedFile.toFile().exists() assertsIs true

	val content = generatedFile.readText()

	// Verify package and object structure
	content.contains("package kore.dependencies.tagcodegentest") assertsIs true
	content.contains("data object TagCodegenTest") assertsIs true
	content.contains("const val NAMESPACE: String = \"tagpack\"") assertsIs true

	// Verify Tags interface is generated
	content.contains("sealed interface Tags") assertsIs true
	content.contains("TagArgument") assertsIs true

	// Verify specific tag enums are generated
	content.contains("sealed interface Block") assertsIs true
	content.contains("data object CustomBlocks") assertsIs true
	content.contains("enum class Mineable") assertsIs true
	content.contains("CUSTOM_PICKAXE") assertsIs true

	content.contains("enum class Item") assertsIs true
	content.contains("CUSTOM_ITEMS") assertsIs true

	content.contains("enum class Enchantment") assertsIs true
	content.contains("POWERFUL") assertsIs true

	// Verify asId() uses # prefix for tags
	content.contains("#\$NAMESPACE:") assertsIs true
}
