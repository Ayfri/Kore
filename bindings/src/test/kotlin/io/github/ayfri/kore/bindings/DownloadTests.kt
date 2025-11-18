package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.bindings.api.exploreDatapacks
import io.github.ayfri.kore.bindings.api.importDatapacks

fun downloadTests() {
	testGitHubMinecraftDefaultData()
}

fun testGitHubMinecraftDefaultData() = newTest("github-minecraft-default-data") {
	println("Downloading pixigeko.minecraft-default-data:1.21.8...")

	try {
		// Test downloading from GitHub without subpath
		val datapacks = exploreDatapacks {
			configuration {
				debug = true
			}

			github("pixigeko.minecraft-default-data:1.21.8")
		}

		println("Explored datapacks: ${datapacks.size}")
		datapacks.forEach { pack ->
			println("  - ${pack.name}: ${pack.functions.size} functions, ${pack.resources.size} resource types")
			pack.resources.forEach { (type, resources) ->
				println("    - $type: ${resources.size} resources")
			}
		}

		(datapacks.size > 0) assertsIs true
		val pack = datapacks[0]
		(pack.functions.size > 0) assertsIs true

		println("✓ GitHub download test passed")
	} catch (e: Exception) {
		println("⚠ GitHub download test skipped: ${e.message}")
		// Don't fail the test if GitHub is unreachable
	}
}
