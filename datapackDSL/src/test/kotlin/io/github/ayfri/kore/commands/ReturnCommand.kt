package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsMatches
import io.github.ayfri.kore.functions.Function

fun Function.returnCommand() {
	returnValue(0) assertsIs "return 0"

	@Suppress("DEPRECATION_ERROR")
	returnRun {
		function("test")
	} assertsIs "return run function ${datapack.name}:test"

	returnIf(0) {
		predicate("test")
	} assertsIs """
		execute if predicate test run return 0
	""".trimIndent()

	@Suppress("DEPRECATION_ERROR")
	returnUnless({
		block(vec3(0, 0, 0), io.github.ayfri.kore.generated.Blocks.STONE)
	}) {
		say("test")
		returnValue(0)
	} assertsMatches Regex(
		"""
		execute unless block 0 0 0 minecraft:stone run return run function ${datapack.name}:${datapack.configuration.generatedFunctionsFolder}/generated_\d+
	""".trimIndent()
	)
}
