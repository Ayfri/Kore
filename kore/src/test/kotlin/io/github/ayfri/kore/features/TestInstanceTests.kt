package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.testinstances.*
import io.github.ayfri.kore.features.testenvironments.enums.Weather
import io.github.ayfri.kore.features.testenvironments.testEnvironmentsBuilder
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument

fun DataPack.testInstanceTests() {
	val environment = testEnvironmentsBuilder.weather("test_env", Weather.CLEAR)

	testInstances {
		testInstance("basic_test") {
			environment(environment)
			maxTicks = 100
			structure(StructureArgument("test_structure", "minecraft"))
			blockBased()
		}

		testInstances.last() assertsIs """
			{
				"environment": "features_tests:test_env",
				"max_ticks": 100,
				"structure": "minecraft:test_structure",
				"type": "block_based"
			}
		""".trimIndent()

		testInstance("full_test") {
			environment(environment)
			maxTicks = 200
			structure(StructureArgument("complex_structure", "minecraft"))
			functionBased()
			function {
				setup(FunctionArgument("test_setup", "minecraft"))
				teardown(FunctionArgument("test_teardown", "minecraft"))
			}
			manualOnly = true
			maxAttempts = 5
			required = true
			requiredSuccesses = 3
			clockwise90()
			setupTicks = 20
			skyAccess = false
		}

		testInstances.last() assertsIs """
			{
				"environment": "features_tests:test_env",
				"function": {
					"setup": "minecraft:test_setup",
					"teardown": "minecraft:test_teardown"
				},
				"manual_only": true,
				"max_attempts": 5,
				"max_ticks": 200,
				"required": true,
				"required_successes": 3,
				"rotation": "clockwise_90",
				"setup_ticks": 20,
				"sky_access": false,
				"structure": "minecraft:complex_structure",
				"type": "function"
			}
		""".trimIndent()
	}
}
