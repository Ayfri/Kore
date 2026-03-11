package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.bindings.api.apiTests
import io.github.cdimascio.dotenv.dotenv

val configuration = dotenv {
	systemProperties = true
}

fun main() {
	println("Running bindings module tests...")
	importingTests()
	importingVanilla()
	apiTests()
	dungeonsAndTavernsTests()
	tagsTests()
	downloadTests()
	writerTests()
	println("All tests passed!")
}
