package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.bindings.api.RemappingState
import io.kotest.core.spec.style.FunSpec
import kotlinx.io.files.Path

fun commonExploreAndRenderTests() {
	testExploreAndRenderInMemoryDatapack()
}

fun testExploreAndRenderInMemoryDatapack() {
	val datapack = InMemoryDatapack(
		mapOf(
			"pack.mcmeta" to """{"pack":{"description":"Test pack","min_format":95,"max_format":95}}""",
			"data/mypack/function/greet.mcfunction" to "say hello",
			"data/mypack/function/sub/nested.mcfunction" to "say nested",
			"data/mypack/enchantment/custom_sharp.json" to "{}",
		)
	)

	val explored = explore(datapack, "in_memory_test", Path("in_memory_test"))

	explored.functions.size assertsIs 2
	explored.functions.any { it.id == "mypack:greet" } assertsIs true
	explored.functions.any { it.id == "mypack:sub/nested" } assertsIs true
	explored.resources["enchantment"]?.size assertsIs 1
	explored.resources["enchantment"]?.get(0)?.id assertsIs "mypack:custom_sharp"

	val (objectName, source) = renderDatapackFile(explored, remappings = RemappingState())

	objectName assertsIs "InMemoryTest"
	source.contains("package kore.dependencies.inmemorytest") assertsIs true
	source.contains("data object InMemoryTest") assertsIs true
	source.contains("const val NAMESPACE: String = \"mypack\"") assertsIs true
	source.contains("sealed interface Functions") assertsIs true
	source.contains("enum class Enchantments") assertsIs true
}

class CommonExploreAndRenderTests : FunSpec({
	test("explore and render") {
		commonExploreAndRenderTests()
	}
})
