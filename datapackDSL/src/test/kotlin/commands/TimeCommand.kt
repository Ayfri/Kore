package commands

import arguments.numbers.TimeType
import assertions.assertsIs
import functions.Function

fun Function.timeTests() {
	time {
		add(1) assertsIs "time add 1"
		query(TimeType.DAYS) assertsIs "time query day"
		set(1) assertsIs "time set 1"
		set(TimePeriod.DAY) assertsIs "time set day"
	}
}
