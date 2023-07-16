package commands

import arguments.maths.vec2
import functions.Function
import utils.assertsIs

fun Function.forceLoadTests() {
	forceLoad {
		add(vec2(0, 0)) assertsIs "forceload add 0 0"
		add(vec2(0, 0), vec2(0, 0)) assertsIs "forceload add 0 0 0 0"

		query() assertsIs "forceload query"
		query(vec2(0, 0)) assertsIs "forceload query 0 0"

		remove(vec2(0, 0)) assertsIs "forceload remove 0 0"
		remove(vec2(0, 0), vec2(0, 0)) assertsIs "forceload remove 0 0 0 0"

		removeAll() assertsIs "forceload remove all"
	}
}
