package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.bindings.api.RemappingState
import io.kotest.core.spec.style.FunSpec
import kotlinx.io.files.Path

fun commonExploreAndRenderTests() {
	testExploreAndRenderInMemoryDatapack()
	testFunctionBindingsUseValidKotlinIdentifiersAndImplementTheirContract()
	testResourceAndTagBindingIdentifiers()
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

fun testFunctionBindingsUseValidKotlinIdentifiersAndImplementTheirContract() {
	val datapack = InMemoryDatapack(
		mapOf(
			"pack.mcmeta" to """{"pack":{"description":"Test pack","min_format":95,"max_format":95}}""",
			"data/mypack/function/_3div.mcfunction" to "",
			"data/mypack/function/_get_double.mcfunction" to "",
			"data/mypack/function/get_double/-3_0.mcfunction" to "",
			"data/mypack/function/get_double/1_2.mcfunction" to "",
			"data/mypack/function/test.mcfunction" to "",
			"data/mypack/function/test/nested.mcfunction" to "",
			"data/mypack/function/only/deep/value.mcfunction" to "",
		)
	)

	val explored = explore(datapack, "function_edge_cases", Path("function_edge_cases"))
	val (_, source) = renderDatapackFile(explored, remappings = RemappingState())

	// FunctionArgument's properties need interface accessors because interfaces cannot hold state.
	source.contains("override val namespace: String = NAMESPACE") assertsIs false
	source.contains("override val namespace: String\n\t\t\tget() = NAMESPACE") assertsIs true
	source.contains("override val name: String\n\t\t\tget() = asId()") assertsIs true
	source.contains("override var directory: String") assertsIs true
	source.contains("set(value) = error(") assertsIs true

	// Leading digits, punctuation, and directory/function collisions must remain valid declarations.
	source.contains("data object N3div : FunctionEdgeCases.Functions") assertsIs true
	source.contains("data object GetDoubleFunction : FunctionEdgeCases.Functions") assertsIs true
	source.contains("data object TestFunction : FunctionEdgeCases.Functions") assertsIs true
	source.contains("data object 3div") assertsIs false
	source.contains("_1_2,") assertsIs true
	source.contains("_3_0,") assertsIs true

	// asId() must use the original Minecraft path, not reconstruct it from a sanitized identifier.
	source.contains("\"\$NAMESPACE:get_double/-3_0\"") assertsIs true
	source.contains("\"\$NAMESPACE:get_double/1_2\"") assertsIs true

	// Directories used only to contain deeper groups are not phantom FunctionArgument instances.
	source.contains("data object Only : FunctionEdgeCases.Functions") assertsIs false
	source.contains("data object Only {") assertsIs true

	val simpleDatapack = InMemoryDatapack(
		mapOf(
			"pack.mcmeta" to """{"pack":{"description":"Test pack","min_format":95,"max_format":95}}""",
			"data/mypack/function/-3_0.mcfunction" to "",
			"data/mypack/function/1_2.mcfunction" to "",
			"data/mypack/function/foo-bar.mcfunction" to "",
			"data/mypack/function/foo.bar.mcfunction" to "",
		)
	)
	val simpleExplored = explore(simpleDatapack, "simple_function_edge_cases", Path("simple_function_edge_cases"))
	val (_, simpleSource) = renderDatapackFile(simpleExplored, remappings = RemappingState())

	simpleSource.contains("enum class Functions : FunctionArgument") assertsIs true
	simpleSource.contains("FOO_BAR,") assertsIs true
	simpleSource.contains("FOO_BAR_FUNCTION,") assertsIs true
	simpleSource.contains("_3_0 -> \"\$NAMESPACE:-3_0\"") assertsIs true
	simpleSource.contains("_1_2 -> \"\$NAMESPACE:1_2\"") assertsIs true
	simpleSource.contains("override var directory: String") assertsIs true
}

fun testResourceAndTagBindingIdentifiers() {
	val datapack = InMemoryDatapack(
		mapOf(
			"pack.mcmeta" to """{"pack":{"description":"Test pack","min_format":95,"max_format":95}}""",
			"data/mypack/recipe/_3div.json" to "{}",
			"data/mypack/recipe/_smelting.json" to "{}",
			"data/mypack/recipe/smelting/-3_0.json" to "{}",
			"data/mypack/recipe/smelting/1_2.json" to "{}",
			"data/mypack/recipe/only/deep/value.json" to "{}",
			"data/mypack/tags/item/mineable/-3_0.json" to "{}",
			"data/mypack/tags/item/mineable/1_2.json" to "{}",
			"data/mypack/tags/item/_3div.json" to "{}",
			"data/mypack/tags/item/only/deep/value.json" to "{}",
		)
	)

	val explored = explore(datapack, "resource_edge_cases", Path("resource_edge_cases"))
	val (_, source) = renderDatapackFile(explored, remappings = RemappingState())

	source.contains("interface Recipes : RecipeArgument {\n\t\toverride val namespace: String = NAMESPACE") assertsIs false
	source.contains("interface Recipes : RecipeArgument {\n\t\toverride val namespace: String\n\t\t\tget() = NAMESPACE") assertsIs true
	source.contains("interface Item : ResourceEdgeCases.Tags, ItemTagArgument {\n\t\t\toverride val namespace: String\n\t\t\t\tget() = NAMESPACE") assertsIs true

	source.contains("data object N3div : ResourceEdgeCases.Recipes") assertsIs true
	source.contains("data object SmeltingResource : ResourceEdgeCases.Recipes") assertsIs true
	source.contains("data object 3div") assertsIs false
	source.contains("_1_2,") assertsIs true
	source.contains("_3_0,") assertsIs true

	source.contains("\"\$NAMESPACE:smelting/-3_0\"") assertsIs true
	source.contains("\"\$NAMESPACE:smelting/1_2\"") assertsIs true

	source.contains("data object Only : ResourceEdgeCases.Recipes") assertsIs false
	source.contains("data object Only {") assertsIs true

	source.contains("data object N3div : ItemTagArgument") assertsIs true
	source.contains("override val name: String = \"_3div\"") assertsIs true
	source.contains("\"#\$NAMESPACE:mineable/-3_0\"") assertsIs true
	source.contains("\"#\$NAMESPACE:mineable/1_2\"") assertsIs true

	source.contains("data object Only : ItemTagArgument") assertsIs false
}

class CommonExploreAndRenderTests : FunSpec({
	test("explore and render") {
		commonExploreAndRenderTests()
	}
})
