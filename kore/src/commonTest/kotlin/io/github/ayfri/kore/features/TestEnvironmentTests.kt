package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.testenvironments.enums.Weather
import io.github.ayfri.kore.features.testenvironments.testEnvironments
import io.github.ayfri.kore.features.timelines.timeline
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Gamerules
import io.github.ayfri.kore.generated.WorldClocks
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

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

		clockTime("clock_time_env", WorldClocks.OVERWORLD, 1000)

		testEnvironments.last() assertsIs """
			{
				"type": "minecraft:clock_time",
				"clock": "minecraft:overworld",
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

		val tl1 = timeline("day_cycle", WorldClocks.OVERWORLD)
		val tl2 = timeline("end_cycle", WorldClocks.THE_END)

		timelineAttributes("timeline_attributes_env", tl1, tl2)

		testEnvironments.last() assertsIs """
			{
				"type": "minecraft:timeline_attributes",
				"timelines": [
					"features_tests:day_cycle",
					"features_tests:end_cycle"
				]
			}
		""".trimIndent()
	}
}

class TestEnvironmentTests : FunSpec({
	test("test environment") {
		dataPack("features_tests") {
			pretty()
			testEnvironmentTests()
		}
	}
})
