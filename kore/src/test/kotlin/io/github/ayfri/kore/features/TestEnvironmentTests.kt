package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.features.testenvironments.enums.Weather
import io.github.ayfri.kore.features.testenvironments.testEnvironments
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Gamerules

fun DataPack.testEnvironmentTests() {
	val testFunction = function("test_environments") {
		say("hi there")
	}

	testEnvironments {
		function("function_env") {
			setup(testFunction)
			teardown(testFunction)
		}

		testEnvironments.last() assertsIs """
			{
				"type": "minecraft:function",
				"setup": "features_tests:test_environments",
				"teardown": "features_tests:test_environments"
			}
		""".trimIndent()

		weather("weather_env", Weather.RAIN)

		testEnvironments.last() assertsIs """
			{
				"type": "minecraft:weather",
				"weather": "rain"
			}
		""".trimIndent()

		timeOfDay("time_env", 1000)

		testEnvironments.last() assertsIs """
			{
				"type": "minecraft:time_of_day",
				"time": 1000
			}
		""".trimIndent()

		gameRules("gamerules_env") {
			this[Gamerules.KEEP_INVENTORY] = false
			this[Gamerules.RANDOM_TICK_SPEED] = 0
		}

		testEnvironments.last() assertsIs """
			{
				"type": "minecraft:game_rules",
				"rules": {
					"minecraft:keep_inventory": false,
					"minecraft:random_tick_speed": 0
				}
			}
		""".trimIndent()
	}
}
