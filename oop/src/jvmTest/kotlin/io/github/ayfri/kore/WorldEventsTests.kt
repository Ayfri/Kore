package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.events.*
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Dimensions
import io.github.ayfri.kore.utils.testDataPack
import io.github.ayfri.kore.world.world
import io.kotest.core.spec.style.FunSpec

fun worldEventsTests() = testDataPack("world_events_tests") {
	val world = world()
	val nether = world(Dimensions.THE_NETHER)

	function("test_world_events") {
		world.onDayStart { say("Day started!") }
		world.onInterval(100.ticks) { say("Every 5 seconds!") }
		world.onLoad { say("World loaded!") }
		world.onMidnight { say("Midnight!") }
		world.onNightStart { say("Night started!") }
		world.onNoon { say("Noon!") }
		world.onRainStart { say("It started raining!") }
		world.onRainStop { say("Rain stopped!") }
		world.onThunderStart { say("Thunder started!") }
		world.onThunderStop { say("Thunder stopped!") }
		world.onTick { say("World tick!") }
		world.onTimeOfDay(1000) { say("Time is 1000!") }

		nether.onRainStart { say("Raining in the Nether... somehow!") }
		nether.onTick { say("Nether tick!") }
	}

	val expectedEvents = listOf(
		"on_day_start", "on_interval", "on_night_start", "on_rain_start", "on_rain_stop",
		"on_thunder_start", "on_thunder_stop", "on_time_of_day_1000", "on_time_of_day_6000",
		"on_time_of_day_18000", "on_world_load", "on_world_tick",
	)

	for (event in expectedEvents) {
		generatedFunctions.any { it.name == OopConstants.dispatchFunctionName(event) } assertsIs true
		functions.any { it.name.startsWith("${event}_handler_") } assertsIs true
	}

	generatedFunctions.any { it.name == OopConstants.worldStateInitFunction } assertsIs true

	val netherTickTag = OopConstants.worldEventTag(OopConstants.worldTickEvent, Dimensions.THE_NETHER.asId())
	generatedFunctions.any { it.name == OopConstants.dispatchFunctionName(netherTickTag) } assertsIs true
	functions.any { it.name.startsWith("${netherTickTag}_handler_") } assertsIs true
}.apply {
	generate()
}

class WorldEventsTests : FunSpec({
	test("world events") {
		worldEventsTests()
	}
})
