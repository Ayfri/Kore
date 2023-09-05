package commands

import arguments.maths.vec3
import assertions.assertsIs
import assertions.assertsMatches
import functions.Function
import generated.Blocks

fun Function.returnCommand() {
	returnValue(0) assertsIs "return 0"
	returnRun {
		function("test")
	} assertsIs "return run function ${datapack.name}:test"

	returnIf(0) {
		predicate("test")
	} assertsIs """
		execute if predicate test run return 0
	""".trimIndent()

	returnUnless({
		block(vec3(0, 0, 0), Blocks.STONE)
	}) {
		say("test")
		returnValue(0)
	} assertsMatches Regex(
		"""
		execute unless block 0 0 0 minecraft:stone run return run function ${datapack.name}:${datapack.configuration.generatedFunctionsFolder}/generated_\d+
	""".trimIndent()
	)
}
