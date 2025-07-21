package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.features.testinstances.*
import io.github.ayfri.kore.features.testenvironments.enums.Weather
import io.github.ayfri.kore.features.testenvironments.testEnvironmentsBuilder
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Structures
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument

fun DataPack.testInstanceTests() {
	val environment = testEnvironmentsBuilder.weather("test_env", Weather.CLEAR)

	val testFunction = function("test_function") {
		say("test_function")
	}

	testInstances {
		testInstance("basic_test") {
			environment(environment)
			maxTicks = 100
			structure(Structures.Village.Common.IRON_GOLEM)
			blockBased()
		}

		testInstances.last() assertsIs """
			{
				"environment": "features_tests:test_env",
				"max_ticks": 100,
				"structure": "minecraft:village/common/iron_golem",
				"type": "block_based"
			}
		""".trimIndent()

		testInstance("full_test") {
			environment(environment)
			maxTicks = 200
			structure(Structures.Igloo.TOP)
			functionBased()
			function {
				setup(testFunction)
				teardown(testFunction)
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
					"setup": "features_tests:test_function",
					"teardown": "features_tests:test_function"
				},
				"manual_only": true,
				"max_attempts": 5,
				"max_ticks": 200,
				"required": true,
				"required_successes": 3,
				"rotation": "clockwise_90",
				"setup_ticks": 20,
				"sky_access": false,
				"structure": "minecraft:igloo/top",
				"type": "function"
			}
		""".trimIndent()
	}
}
