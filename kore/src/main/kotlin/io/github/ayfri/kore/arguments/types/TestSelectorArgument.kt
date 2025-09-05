package io.github.ayfri.kore.arguments.types

import io.github.ayfri.kore.arguments.Argument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data class TestSelectorArgument(val pattern: String) : Argument {
	init {
		require(pattern.isNotEmpty()) { "Test selector pattern cannot be empty" }
	}

	override fun asString(): String {
		// If namespace is not supplied, defaults to minecraft
		return if (':' in pattern) pattern else "minecraft:$pattern"
	}
}

/**
 * Creates a test selector with a specific pattern.
 * Supports * and ? for matching namespaced IDs.
 * If namespace is not supplied, defaults to minecraft.
 * Examples:
 * - `*:*` matches all IDs
 * - `*` matches everything in the minecraft namespace
 * - `custom:folder/\*_test_?` matches IDs in the folder of the custom namespace,
 *   with a prefix followed by test followed by a single valid character
 */
fun testSelector(pattern: String) = TestSelectorArgument(pattern)

/** Creates a test selector from a resource location. */
fun testSelector(resourceLocation: ResourceLocationArgument, prefix: String = "", suffix: String = "") = TestSelectorArgument("$prefix${resourceLocation.asId()}$suffix")

/** Creates a test selector that matches all IDs in all namespaces. */
fun allTests() = TestSelectorArgument("*:*")

/** Creates a test selector that matches everything in the minecraft namespace. */
fun minecraftTests() = TestSelectorArgument("minecraft:*")
