package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.features.testenvironments.enums.Weather
import io.github.ayfri.kore.features.testenvironments.testEnvironmentsBuilder
import io.github.ayfri.kore.features.testinstances.testInstances
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Structures

fun DataPack.testInstanceTests() {
	val environment = testEnvironmentsBuilder.weather("test_env", Weather.CLEAR)

	function("test_function") {
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
			function("io.ayfri.kore.mod.MyMod::my_function")
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
				"function": "io.ayfri.kore.mod.MyMod::my_function",
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
