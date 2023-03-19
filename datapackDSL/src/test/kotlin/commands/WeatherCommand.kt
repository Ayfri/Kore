package commands

import arguments.numbers.days
import functions.Function
import utils.assertsIs

fun Function.weatherTests() {
	weatherClear() assertsIs "weather clear"
	weatherRain() assertsIs "weather rain"
	weatherThunder() assertsIs "weather thunder"
	weatherClear(100) assertsIs "weather clear 100"
	weatherRain(100) assertsIs "weather rain 100"
	weatherThunder(100) assertsIs "weather thunder 100"
	weatherClear(1.0.days) assertsIs "weather clear 1d"
	weatherRain(1.0.days) assertsIs "weather rain 1d"
	weatherThunder(1.0.days) assertsIs "weather thunder 1d"
}
