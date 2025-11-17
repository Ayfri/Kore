package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.bindings.api.apiTests

fun main() {
	println("Running bindings module tests...")
	importingTests()
	importingVanilla()
	apiTests()
	dungeonsAndTavernsTests()
	tagsTests()
	println("All tests passed!")
}
