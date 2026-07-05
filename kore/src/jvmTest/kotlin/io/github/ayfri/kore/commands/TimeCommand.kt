package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.TimeType
import io.github.ayfri.kore.arguments.numbers.days
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.types.resources.timeMarker
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generate
import io.github.ayfri.kore.generated.Timelines
import io.github.ayfri.kore.generated.arguments.types.WorldClockArgument
import io.kotest.core.spec.style.FunSpec

fun Function.timeTests() {
	time {
		add(1) assertsIs "time add 1"
		add(1.seconds) assertsIs "time add 1s"
		query(TimeType.DAYS) assertsIs "time query day"
		set(1) assertsIs "time set 1"
		set(TimePeriod.DAY) assertsIs "time set day"
	}

	time.set(1.days) assertsIs "time set 1d"
	time.pause() assertsIs "time pause"
	time.resume() assertsIs "time resume"
	time.query(Timelines.DAY) assertsIs "time query minecraft:day"
	time.queryRepetitions(Timelines.DAY) assertsIs "time query minecraft:day repetitions"
	time.queryTime() assertsIs "time query time"
	time.set(timeMarker("moon")) assertsIs "time set minecraft:moon"
	time.rate(2.0f) assertsIs "time rate 2"

	// TimeWithClock - via time.of(clock)
	val clock = WorldClockArgument("overworld")
	time.of(clock).add(1) assertsIs "time of minecraft:overworld add 1"
	time.of(clock).add(1.seconds) assertsIs "time of minecraft:overworld add 1s"
	time.of(clock).pause() assertsIs "time of minecraft:overworld pause"
	time.of(clock).resume() assertsIs "time of minecraft:overworld resume"
	time.of(clock).query(TimeType.DAYS) assertsIs "time of minecraft:overworld query day"
	time.of(clock).query(Timelines.DAY) assertsIs "time of minecraft:overworld query minecraft:day"
	time.of(clock)
		.queryRepetitions(Timelines.DAY) assertsIs "time of minecraft:overworld query minecraft:day repetitions"
	time.of(clock).queryTime() assertsIs "time of minecraft:overworld query time"
	time.of(clock).set(1) assertsIs "time of minecraft:overworld set 1"
	time.of(clock).set(TimePeriod.DAY) assertsIs "time of minecraft:overworld set day"
	time.of(clock).set(timeMarker("moon")) assertsIs "time of minecraft:overworld set minecraft:moon"
	time.of(clock).rate(2.0f) assertsIs "time of minecraft:overworld rate 2"
}

class TimeCommandTests : FunSpec({
	test("time") {
		dataPack("unit_tests") {
			load { timeTests() }
		}.generate()
	}
})
