package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.days
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.tickTests() {
	tick {
		freeze() assertsIs "tick freeze"
		query() assertsIs "tick query"
		rate(1f) assertsIs "tick rate 1"
		sprint(1) assertsIs "tick sprint 1"
		sprint(1.days) assertsIs "tick sprint 1d"
		sprintStop() assertsIs "tick sprint stop"
		stepStop() assertsIs "tick step stop"
		step(1) assertsIs "tick step 1"
		step(1.days) assertsIs "tick step 1d"
		unfreeze() assertsIs "tick unfreeze"
	}
}
