package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.TimeType
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.timeTests() {
	time {
		add(1) assertsIs "time add 1"
		query(TimeType.DAYS) assertsIs "time query day"
		set(1) assertsIs "time set 1"
		set(TimePeriod.DAY) assertsIs "time set day"
	}
}
